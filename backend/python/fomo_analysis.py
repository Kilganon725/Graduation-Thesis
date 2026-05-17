import json
from pathlib import Path

import pandas as pd


def load_data(csv_path: str) -> pd.DataFrame:
    return pd.read_csv(csv_path)


def anxiety_distribution(df: pd.DataFrame):
    return df["anxiety_level"].value_counts().to_dict()


def ai_usage_frequency(df: pd.DataFrame):
    bins = [0, 5, 10, 15, 20]
    labels = ["低", "中", "高", "很高"]
    grouped = pd.cut(df["ai_usage"], bins=bins, labels=labels, include_lowest=True, right=True)
    return grouped.value_counts().sort_index().to_dict()


def learning_trend(df: pd.DataFrame):
    df = df.sort_values("created_time")
    df["learning_index"] = 20 - df["short_video_time"]
    return df[["created_time", "learning_index"]].to_dict(orient="records")


if __name__ == "__main__":
    file = Path(__file__).parent / "sample_fomo.csv"
    if file.exists():
        data = load_data(str(file))
        print(json.dumps({
            "anxiety_distribution": anxiety_distribution(data),
            "ai_usage_frequency": ai_usage_frequency(data),
            "learning_trend": learning_trend(data),
        }, ensure_ascii=False, indent=2))
    else:
        print("sample_fomo.csv not found")
