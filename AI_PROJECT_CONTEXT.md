# AI Project Context - learn-java_huangnv

> **AI 提示**: 这是项目的核心上下文文档，请优先阅读此文件。每次对项目进行修改后，必须更新下方的「变更日志」部分。

---

## 项目概述

这是 **West2 Online 工作室 Java 方向考核学习项目**，由 huangnv 开发。

- **构建工具**: Maven (Java 1.8)
- **主要依赖**: jsoup 1.17.2, gson 2.10.1, junit-jupiter 5.10.1

---

## 目录结构

```
learn-java_huangnv/
├── pom.xml                          # Maven 配置
├── AI_PROJECT_CONTEXT.md            # [本文件] AI 上下文说明
├── README.md                        # 项目总览
│
├── docs/                            # 学习文档 (0-7 阶段考核指南)
├── etc/blog/                        # 技术博客 (Docker/MySQL/Redis等)
│
├── stage_1/src/                     # 第一阶段：基础语法 [已完成]
│   ├── PetShop/                     # 核心任务：宠物店系统
│   └── Bonus/                       # 加分项目
│
├── stage_2/                         # 第二阶段：DWASearch 命令行程序 [进行中]
│   ├── data/                        # 已爬取的 JSON 数据
│   │   ├── athletes.json
│   │   ├── result.json
│   │   └── event_*.json
│   └── src/
│       ├── DWASearch.java           # 程序入口 (待实现)
│       │
│       ├── core/                    # [门面] 对外 API (待实现)
│       │   └── CoreModule.java
│       │
│       ├── command/                 # [横切] 命令解析 (待实现) ← 下一步
│       │   ├── Command.java
│       │   ├── CommandType.java
│       │   └── CommandParser.java
│       │
│       ├── scheduler/               # [横切] 调度与依赖分析 (待实现)
│       │   ├── DependencyAnalyzer.java
│       │   ├── TaskScheduler.java
│       │   └── TaskGroup.java
│       │
│       ├── cache/                   # [横切] 缓存层 (待实现)
│       │   ├── CacheKey.java
│       │   └── LRUCache.java
│       │
│       ├── common/                  # [公共] 接口定义 [已完成]
│       │   ├── DataFetcher.java     # 接口: String fetch() throws IOException
│       │   └── OutputFormatter.java # 接口: String format(String json, String option)
│       │
│       └── DataCrawler/             # [业务] 爬虫和格式化 [已完成接口实现]
│           ├── CrawlerService.java  # (可删除，未使用)
│           ├── Repository.java      # (可删除，未使用)
│           ├── Athlete/
│           │   ├── AthleteCrawler.java   # implements DataFetcher [已完成]
│           │   └── AthleteFormatter.java # implements OutputFormatter [已完成]
│           └── Result/
│               ├── ResultCrawler.java    # implements DataFetcher [已完成]
│               └── ResultFormatter.java  # implements OutputFormatter [已完成]
│
└── out/, target/                    # 编译输出 (可忽略)
```

---

## Stage 2 详细进度

### 已完成的工作

#### 1. 架构设计 ✓

设计了**混合架构**：
- **横切层**（按职责分包）：command、scheduler、cache、core
- **业务层**（按域分包）：DataCrawler/Athlete、DataCrawler/Result
- **公共接口**：common/ 连接横切层和业务层

```
┌─────────────────────────────────────────────────────────────┐
│                      DWASearch.java                         │
│                         (入口)                              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     core/CoreModule                         │
│              execute(String cmd) 统一门面                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────┬──────────────┬──────────────┐
│   command/   │  scheduler/  │    cache/    │  ← 横切层
│  命令解析     │  依赖分析     │   LRU缓存    │
│              │  多线程调度   │   (容量=3)   │
└──────────────┴──────────────┴──────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    common/ 公共接口                          │
│         DataFetcher (爬虫接口)  OutputFormatter (格式化接口)  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                 DataCrawler/ 业务实现                        │
│      Athlete/                        Result/                │
│  AthleteCrawler (Fetcher)      ResultCrawler (Fetcher)     │
│  AthleteFormatter (Formatter)  ResultFormatter (Formatter)  │
└─────────────────────────────────────────────────────────────┘
```

#### 2. 接口定义 ✓

**DataFetcher 接口** (`common/DataFetcher.java`)：
```java
public interface DataFetcher {
    public String fetch() throws IOException;
}
```
- 职责：去 API 爬取数据，返回 JSON 字符串
- 不关心数据怎么展示

**OutputFormatter 接口** (`common/OutputFormatter.java`)：
```java
public interface OutputFormatter {
    public String format(String json, String option);
}
```
- 职责：把 JSON 格式化成输出字符串
- `option` 参数用于区分输出模式（如 `""` 简略 / `"detail"` 详细）

#### 3. 业务层实现 ✓

**AthleteCrawler** (`DataCrawler/Athlete/AthleteCrawler.java`)：
- 实现 `DataFetcher` 接口
- `fetch()` 爬取运动员列表 API
- 保留了 `saveJsonToFile()` 等辅助方法

**AthleteFormatter** (`DataCrawler/Athlete/AthleteFormatter.java`)：
- 实现 `OutputFormatter` 接口
- `format(json, option)`：option 为 `""` 时调用 `formatAllPlayers()`
- 输出格式：`Full Name:xxx\nGender:xxx\nCountry:xxx\n-----\n`

**ResultCrawler** (`DataCrawler/Result/ResultCrawler.java`)：
- 实现 `DataFetcher` 接口
- `fetch()` 爬取比赛结果汇总 API
- `fetchEventJsonByDisciplineName(name)` 爬取具体项目的详细数据

**ResultFormatter** (`DataCrawler/Result/ResultFormatter.java`)：
- 实现 `OutputFormatter` 接口
- `format(json, option)`：
  - `option = ""`：调用 `formatFinalResults()` 只输出决赛
  - `option = "detail"`：调用 `formatDetailedResults()` 输出初赛+半决赛+决赛

#### 4. 清理工作 ✓

- 删除了 `AthleteService.java`（职责由 CoreModule 承担）
- 删除了 `ResultService.java`（职责由 CoreModule 承担）
- 删除了多余的 `athlete/` 和 `result/` 空包（直接复用 DataCrawler）

---

### 接下来要做的工作

#### 第一步：command/ 命令解析模块 ← 建议从这里开始

**职责**：把 input.txt 的字符串解析成 Command 对象

**需要实现的文件**：

1. **CommandType.java** - 命令类型枚举
   ```
   PLAYERS        - players 命令
   RESULT         - result xxx 命令（简略）
   RESULT_DETAIL  - result xxx detail 命令（详细）
   ERROR          - 无法识别的命令 → 输出 "Error"
   NA             - 无效的项目名称 → 输出 "N/A"
   ```

2. **Command.java** - 命令实体类
   ```
   属性：
   - lineNumber: int      原始行号（保持输出顺序）
   - rawCommand: String   原始命令字符串
   - type: CommandType    命令类型
   - eventName: String    项目名称（仅 RESULT/RESULT_DETAIL 有效）
   - isDetail: boolean    是否详细模式
   ```

3. **CommandParser.java** - 解析逻辑
   ```
   方法：
   - parse(String inputPath) → List<Command>  读取文件，解析所有命令
   - parseLine(String line, int lineNumber) → Command  解析单行命令

   有效项目名称列表：
   - women 1m springboard
   - women 3m springboard
   - women 10m platform
   - women 3m synchronised
   - women 10m synchronised
   - men 1m springboard
   - men 3m springboard
   - men 10m platform
   - men 3m synchronised
   - men 10m synchronised
   ```

**解析流程示例**：
```
输入: "result women 1m springboard detail"
      ↓
解析:
  - 以空格分割
  - 第一个词是 "result" → 是结果查询
  - 最后一个词是 "detail" → 详细模式
  - 中间部分 "women 1m springboard" → 项目名
  - 检查项目名是否在有效列表中
      ↓
输出: Command {
  type: RESULT_DETAIL,
  eventName: "women 1m springboard",
  isDetail: true
}
```

#### 第二步：cache/ 缓存模块

**CacheKey.java**：
- 缓存键，用于标识数据源
- 如：`ATHLETES`、`RESULT_women_1m_springboard`

**LRUCache.java**：
- 继承 `LinkedHashMap`，容量为 3
- 最近最少使用的自动淘汰

#### 第三步：scheduler/ 调度模块

**DependencyAnalyzer.java**：
- 分析命令列表，找出哪些命令共享同一数据源
- 返回 `Map<CacheKey, TaskGroup>`

**TaskGroup.java**：
- 一组共享数据源的命令
- 只需爬取一次，结果复用

**TaskScheduler.java**：
- 多线程执行不同的 TaskGroup
- 保证输出按原始命令顺序

#### 第四步：core/CoreModule.java

- 对外暴露 `execute(String cmd)` 和 `executeBatch(List<String> commands)`
- 内部串联 CommandParser → DependencyAnalyzer → Cache → Fetcher → Formatter

#### 第五步：DWASearch.java

- 程序入口
- 读取命令行参数 `input.txt` 和 `output.txt`
- 调用 CoreModule，写入输出文件

#### 第六步：单元测试

- 至少 10 个测试用例
- 覆盖正常命令、错误命令、边界情况

#### 第七步：打包

- 打包成 `DWASearch.jar`
- 可通过 `java -jar DWASearch.jar input.txt output.txt` 运行

---

### 核心概念理解

#### Fetcher vs Formatter 职责划分

```
Fetcher 的职责：去哪里拿数据
- 不关心 detail 还是非 detail
- 只管返回 JSON

Formatter 的职责：数据怎么展示
- 根据 option 参数决定输出格式
- detail 和非 detail 拿到的 JSON 是一样的，区别在 Formatter 怎么解析
```

#### 整体数据流

```
input.txt
    ↓
[CommandParser] 解析命令
    ↓
List<Command>
    ↓
[DependencyAnalyzer] 按数据源分组
    ↓
Map<CacheKey, TaskGroup>
    ↓
[TaskScheduler] 多线程执行
    ↓
对每个 TaskGroup:
    ├─ [Cache] 检查是否命中
    ├─ [Fetcher] 未命中则爬取
    ├─ [Cache] 存入缓存
    └─ [Formatter] 格式化输出
    ↓
按原始顺序拼接结果
    ↓
output.txt
```

---

## 关键文件快速定位

| 用途 | 文件路径 | 状态 |
|------|----------|------|
| **接口** | | |
| DataFetcher | `stage_2/src/common/DataFetcher.java` | ✓ 已完成 |
| OutputFormatter | `stage_2/src/common/OutputFormatter.java` | ✓ 已完成 |
| **业务实现** | | |
| 运动员爬虫 | `stage_2/src/DataCrawler/Athlete/AthleteCrawler.java` | ✓ 已完成 |
| 运动员格式化 | `stage_2/src/DataCrawler/Athlete/AthleteFormatter.java` | ✓ 已完成 |
| 结果爬虫 | `stage_2/src/DataCrawler/Result/ResultCrawler.java` | ✓ 已完成 |
| 结果格式化 | `stage_2/src/DataCrawler/Result/ResultFormatter.java` | ✓ 已完成 |
| **横切模块** | | |
| 命令解析 | `stage_2/src/command/` | 待实现 |
| 缓存 | `stage_2/src/cache/` | 待实现 |
| 调度器 | `stage_2/src/scheduler/` | 待实现 |
| **入口** | | |
| CoreModule | `stage_2/src/core/CoreModule.java` | 待实现 |
| DWASearch | `stage_2/src/DWASearch.java` | 待实现 |

---

## AI 操作指南

### 修改代码后必做

1. **更新变更日志**: 在下方「变更日志」添加新条目
2. **格式要求**:
   ```
   ### YYYY-MM-DD HH:MM - [简要标题]
   - **修改文件**: 列出修改的文件路径
   - **修改内容**: 描述做了什么
   - **注意事项**: 后续 AI 需要注意的点 (可选)
   ```

### 与用户协作原则

- **用户是学习者**：这是用户的作业，AI 应该引导而不是代写
- **只提供骨架**：创建文件时只提供空类/接口定义，不写具体实现
- **解释概念**：用简单的比喻解释架构和设计模式
- **回答问题**：耐心解答用户的疑问，引导用户思考

---

## 变更日志

> **AI 提示**: 每次修改项目后，在此处添加记录，最新的放在最上面。

### 2026-01-19 - 完成接口层和业务层实现

- **修改文件**:
  - `common/DataFetcher.java` - 定义接口
  - `common/OutputFormatter.java` - 定义接口
  - `DataCrawler/Athlete/AthleteCrawler.java` - 实现 DataFetcher
  - `DataCrawler/Athlete/AthleteFormatter.java` - 实现 OutputFormatter
  - `DataCrawler/Result/ResultCrawler.java` - 实现 DataFetcher
  - `DataCrawler/Result/ResultFormatter.java` - 实现 OutputFormatter
  - 删除 `AthleteService.java` 和 `ResultService.java`
  - 删除多余的 `athlete/` 和 `result/` 空包
- **修改内容**:
  - 定义了两个核心接口
  - 让现有爬虫和格式化类实现接口
  - 清理了不再需要的 Service 层
- **设计决策**:
  - `DataFetcher.fetch()` 只负责拿数据，不关心展示
  - `OutputFormatter.format(json, option)` 根据 option 参数决定输出格式
  - detail 信息通过 option 参数传递给 Formatter，不影响 Fetcher
- **下一步**: 实现 command/ 命令解析模块

### 2026-01-19 - 设计 DWASearch 架构并创建文件结构

- **修改文件**:
  - 更新 `AI_PROJECT_CONTEXT.md`
  - 新建 `stage_2/src/` 下的目录和文件结构
- **修改内容**:
  - 设计混合架构：横切层（command/scheduler/cache）+ 业务层（DataCrawler）
  - 创建空文件结构，待填充代码
- **架构决策**:
  - 对外 API 只暴露 `CoreModule.execute(String cmd)`，内部自动解析路由
  - 通过 `common/` 包的接口连接横切层和业务层
  - 新增业务只需新建包并实现接口

### 2026-01-19 - 初始化 AI 上下文文档

- **修改文件**: 新建 `AI_PROJECT_CONTEXT.md`
- **修改内容**: 创建项目说明文档，包含目录结构、模块说明、关键文件定位
- **当前状态**:
  - Stage 1 (PetShop + Bonus) 已完成
  - Stage 2 (DataCrawler) 已完成基础爬虫功能
  - 已爬取运动员和比赛结果数据

---

## 待办事项

> 按优先级排序

- [x] 定义 `common/DataFetcher.java` 接口
- [x] 定义 `common/OutputFormatter.java` 接口
- [x] `AthleteCrawler` 实现 `DataFetcher`
- [x] `AthleteFormatter` 实现 `OutputFormatter`
- [x] `ResultCrawler` 实现 `DataFetcher`
- [x] `ResultFormatter` 实现 `OutputFormatter`
- [x] 删除 Service 层
- [ ] **实现 `command/CommandType.java` 枚举** ← 下一步
- [ ] **实现 `command/Command.java` 实体类**
- [ ] **实现 `command/CommandParser.java` 解析逻辑**
- [ ] 实现 `cache/CacheKey.java`
- [ ] 实现 `cache/LRUCache.java`
- [ ] 实现 `scheduler/DependencyAnalyzer.java`
- [ ] 实现 `scheduler/TaskGroup.java`
- [ ] 实现 `scheduler/TaskScheduler.java`
- [ ] 实现 `core/CoreModule.java`
- [ ] 实现 `DWASearch.java`
- [ ] 编写至少 10 个单元测试
- [ ] 打包成 `DWASearch.jar`

---

*最后更新: 2026-01-19*
