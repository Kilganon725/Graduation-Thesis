export function scoreShortVideoMinutes(minutes) {
  if (minutes <= 15) return 0
  if (minutes <= 30) return 5
  if (minutes <= 60) return 10
  if (minutes <= 120) return 15
  return 20
}

export function scoreLearningSwitchTimes(times) {
  if (times <= 1) return 0
  if (times <= 3) return 5
  if (times <= 5) return 10
  if (times <= 8) return 15
  return 20
}

export function scoreAnxietyFrequency(frequency) {
  if (frequency <= 1) return 0
  if (frequency === 2) return 5
  if (frequency === 3) return 10
  if (frequency === 4) return 15
  return 20
}

export function scoreAiUsageTimes(times) {
  if (times === 0) return 0
  if (times <= 2) return 5
  if (times <= 5) return 10
  if (times <= 10) return 15
  return 20
}

export function scorePreview(form) {
  const shortVideoScore = scoreShortVideoMinutes(Number(form.shortVideoMinutes || 0))
  const learningSwitchScore = scoreLearningSwitchTimes(Number(form.learningSwitchTimes || 0))
  const anxietyScore = scoreAnxietyFrequency(Number(form.anxietyFrequency || 0))
  const aiUsageScore = scoreAiUsageTimes(Number(form.aiUsageTimes || 0))
  const totalScore = shortVideoScore + learningSwitchScore + anxietyScore + aiUsageScore
  const level = totalScore <= 30 ? '正常' : totalScore <= 60 ? '轻度焦虑' : totalScore <= 80 ? '中度焦虑' : '高度焦虑'
  return {
    shortVideoScore,
    learningSwitchScore,
    anxietyScore,
    aiUsageScore,
    totalScore,
    level
  }
}
