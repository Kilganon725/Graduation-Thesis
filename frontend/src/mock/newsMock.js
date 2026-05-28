function imageSeed(category, index) {
  return `https://picsum.photos/seed/news-${category}-${index}/900/600`
}

function baseArticles(category) {
  const presets = {
    ai: [
      {
        title: 'OpenAI 发布新一代模型能力更新',
        description: '围绕多模态、推理和工具调用的能力持续增强，AI 产品形态进入更强的实用阶段。',
        url: 'https://openai.com/news/',
        sourceName: 'OpenAI Blog'
      },
      {
        title: 'Google AI 强调模型安全与代理能力',
        description: 'Google 持续推进 Gemini 生态与 AI Agent 的落地，聚焦安全、效率与跨场景应用。',
        url: 'https://blog.google/innovation-and-ai/technology/ai/',
        sourceName: 'Google AI Blog'
      },
      {
        title: 'Hugging Face 社区更新开源模型与工具链',
        description: '开源 AI 工具、数据集与模型更新频繁，开发者生态继续扩张。',
        url: 'https://huggingface.co/blog',
        sourceName: 'Hugging Face Blog'
      }
    ],
    tech: [
      {
        title: '芯片与算力基础设施继续拉动科技投资',
        description: 'AI 推理需求推动算力、散热、存储和云基础设施持续升级。',
        url: 'https://techcrunch.com/tag/artificial-intelligence/',
        sourceName: 'TechCrunch'
      },
      {
        title: '机器人与自动化正在进入更多消费级场景',
        description: '从家庭到工业，智能硬件与机器人应用正在扩展到更广的落地场景。',
        url: 'https://techcrunch.com/tag/robotics/',
        sourceName: 'TechCrunch'
      },
      {
        title: '科技创业公司继续围绕 AI 形成新产品线',
        description: '围绕 AI 的创业项目正在覆盖教育、办公、开发工具和内容生产等场景。',
        url: 'https://techcrunch.com/tag/artificial-intelligence/',
        sourceName: 'TechCrunch'
      }
    ],
    research: [
      {
        title: 'AI 研究持续聚焦推理、评测与对齐',
        description: '研究方向正从单纯参数规模转向可控性、可靠性和评估体系构建。',
        url: 'https://huggingface.co/blog',
        sourceName: 'Hugging Face Blog'
      },
      {
        title: '开源社区推动模型微调与部署工具成熟',
        description: '更多研究成果开始落地到轻量部署、推理优化和本地化应用。',
        url: 'https://blog.google/innovation-and-ai/technology/ai/',
        sourceName: 'Google AI Blog'
      },
      {
        title: '学术与产业共同推进 AI 应用边界',
        description: 'AI 研究的重点正从“能不能做”转向“能不能稳定、安全地做”。',
        url: 'https://openai.com/news/',
        sourceName: 'OpenAI Blog'
      }
    ]
  }
  return presets[category] || presets.ai
}

export function createMockNewsFeed(category = 'ai', keyword = '', pageSize = 9) {
  const items = baseArticles(category)
  const articles = Array.from({ length: pageSize }, (_, index) => {
    const seed = items[index % items.length]
    return {
      title: seed.title,
      description: keyword ? `${seed.description} 关键词：${keyword}` : seed.description,
      content: seed.description,
      url: seed.url,
      urlToImage: imageSeed(category, index),
      sourceName: seed.sourceName,
      publishedAt: new Date(Date.now() - index * 3600 * 1000).toISOString(),
      category
    }
  })

  return {
    sourceName: '本地新闻回退',
    category,
    keyword,
    total: articles.length,
    mock: true,
    fetchedAt: new Date().toISOString(),
    articles
  }
}
