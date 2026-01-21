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
│       ├── Core/                    # [门面] 对外 API (待实现)
│       │   └── CoreModule.java
│       │
│       ├── command/                 # [横切] 命令解析 (待实现) ← 下一步
│       │   ├── Command.java
│       │   ├── CommandType.java
│       │   └── CommandParser.java
│       │
│       ├── Scheduler/               # [横切] 调度与依赖分析 (待实现)
│       │   ├── DependencyAnalyzer.java
│       │   ├── TaskScheduler.java
│       │   └── TaskGroup.java
│       │
│       ├── cache/                   # [横切] 缓存层 (待实现)
│       │   ├── CacheKey.java
│       │   └── LRUCache.java
│       │
│       ├── Common/                  # [公共] 接口定义 [已完成]
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
- **横切层**（按职责分包）：command、Scheduler、cache、Core
- **业务层**（按域分包）：DataCrawler/Athlete、DataCrawler/Result
- **公共接口**：Common/ 连接横切层和业务层

```
┌─────────────────────────────────────────────────────────────┐
│                      DWASearch.java                         │
│                         (入口)                              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     Core/CoreModule                         │
│              execute(String cmd) 统一门面                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────┬──────────────┬──────────────┐
│   command/   │  Scheduler/  │    cache/    │  ← 横切层
│  命令解析     │  依赖分析     │   LRU缓存    │
│              │  多线程调度   │   (容量=3)   │
└──────────────┴──────────────┴──────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Common/ 公共接口                          │
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

**DataFetcher 接口** (`Common/DataFetcher.java`)：
```java
public interface DataFetcher {
    public String fetch() throws IOException;
}
```
- 职责：去 API 爬取数据，返回 JSON 字符串
- 不关心数据怎么展示

**OutputFormatter 接口** (`Common/OutputFormatter.java`)：
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

#### 第三步：Scheduler/ 调度模块

**DependencyAnalyzer.java**：
- 分析命令列表，找出哪些命令共享同一数据源
- 返回 `Map<CacheKey, TaskGroup>`

**TaskGroup.java**：
- 一组共享数据源的命令
- 只需爬取一次，结果复用

**TaskScheduler.java**：
- 多线程执行不同的 TaskGroup
- 保证输出按原始命令顺序

#### 第四步：Core/CoreModule.java

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

#### 多线程执行框架（调度器示意）

**目标**：同一数据源只抓取一次，按原始行号输出，保证并发安全与顺序正确。

**核心数据结构**（示意）：
```
Task {
  line: int
  key: String          // ATHLETES / EVENT:<eventName>
  option: String       // "" or "detail"
}
```

**分组执行思路**：
1. CoreModule 先把 Error/N/A 直接写入输出数组。
2. 对剩余 Task 按 `key` 分组（同 key 代表共享数据源）。
3. 每个 key 对应一个执行单元（线程或线程池任务）。
4. 线程内部：
   - 按 key 判断抓取逻辑：
     - `ATHLETES` → `AthleteCrawler.fetch()`
     - `EVENT:<eventName>` → `ResultCrawler.fetchEventJsonByDisciplineName(eventName)`
   - 对组内每个 Task 使用对应 Formatter：
     - `option=""` → 简略输出
     - `option="detail"` → 详细输出
   - 写回 `outStringArray[line-1]`
5. 主线程等待所有任务结束后按行号拼接输出。

**并发安全建议**：
- 任务只写自己的 `line` 位置，且主线程等待所有任务完成后再读取。
- 如果使用多线程写数组，建议：
  - `AtomicReferenceArray<String>` 或
  - 任务返回结果，主线程 `Future.get()` 后写入数组（更简单）。

**示例分组**：
```
players
result women 1m springboard
result women 1m springboard detail
players
```
分组后：
- key=ATHLETES: line 1, line 4（只抓一次运动员）
- key=EVENT:women 1m springboard: line 2, line 3（只抓一次项目结果）

--- 

## 关键文件快速定位

| 用途 | 文件路径 | 状态 |
|------|----------|------|
| **接口** | | |
| DataFetcher | `stage_2/src/Common/DataFetcher.java` | ✓ 已完成 |
| OutputFormatter | `stage_2/src/Common/OutputFormatter.java` | ✓ 已完成 |
| **业务实现** | | |
| 运动员爬虫 | `stage_2/src/DataCrawler/Athlete/AthleteCrawler.java` | ✓ 已完成 |
| 运动员格式化 | `stage_2/src/DataCrawler/Athlete/AthleteFormatter.java` | ✓ 已完成 |
| 结果爬虫 | `stage_2/src/DataCrawler/Result/ResultCrawler.java` | ✓ 已完成 |
| 结果格式化 | `stage_2/src/DataCrawler/Result/ResultFormatter.java` | ✓ 已完成 |
| **横切模块** | | |
| 命令解析 | `stage_2/src/command/` | 待实现 |
| 缓存 | `stage_2/src/cache/` | 待实现 |
| 调度器 | `stage_2/src/Scheduler/` | 待实现 |
| **入口** | | |
| CoreModule | `stage_2/src/Core/CoreModule.java` | 待实现 |
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

[//]: # (- **用户是学习者**：这是用户的作业，AI 应该引导而不是代写)

[//]: # (- **只提供骨架**：创建文件时只提供空类/接口定义，不写具体实现)

[//]: # (- **解释概念**：用简单的比喻解释架构和设计模式)

[//]: # (- **回答问题**：耐心解答用户的疑问，引导用户思考)

---

## 变更日志

> **AI 提示**: 每次修改项目后，在此处添加记录，最新的放在最上面。

### 2026-01-20 22:56 - 处理 Competitors 为空的结果记录

- **修改文件**:
  - `stage_2/src/DataCrawler/Result/ResultFormatter.java`
- **修改内容**:
  - 在 Competitors 不是数组时回退到 FullName/LastName/FirstName

### 2026-01-20 22:47 - 项目名称改为官方格式

- **修改文件**:
  - `stage_2/src/Command/CommandParser.java`
  - `stage_2/input.txt`
- **修改内容**:
  - 命令解析的有效项目名改为官方名称
  - 示例输入同步更新为官方大小写

### 2026-01-20 22:41 - 默认输入输出指向 stage_2

- **修改文件**:
  - `stage_2/src/DWASearch.java`
- **修改内容**:
  - 无参数时默认读取 `stage_2/input.txt` 并写入 `stage_2/output.txt`

### 2026-01-20 22:36 - DWASearch 默认输入输出路径

- **修改文件**:
  - `stage_2/src/DWASearch.java`
- **修改内容**:
  - 当无参数时默认读取 `input.txt` 并写入 `output.txt`

### 2026-01-20 22:34 - 完善入口与提供多组调度示例

- **修改文件**:
  - `stage_2/src/DWASearch.java`
  - `stage_2/input_sample.txt`
- **修改内容**:
  - DWASearch 支持读取输入输出路径并写入结果
  - 添加 7 行示例命令以覆盖多组调度场景

### 2026-01-20 22:29 - CoreModule 组装调度流程

- **修改文件**:
  - `stage_2/src/Core/CoreModule.java`
- **修改内容**:
  - 接入依赖分析与调度器执行
  - 统一拼接输出字符串

### 2026-01-20 22:25 - 查询门面调整为各自包与公共接口

- **修改文件**:
  - `stage_2/src/Common/QueryService.java` - 查询接口移入 Common
  - `stage_2/src/DataCrawler/Athlete/AthleteQueryService.java` - 运动员门面移入 Athlete 包
  - `stage_2/src/DataCrawler/Result/ResultQueryService.java` - 结果门面移入 Result 包
  - `stage_2/src/Scheduler/TaskScheduler.java` - 更新导入路径
- **修改内容**:
  - 将门面服务按领域放回各自包，接口放入公共包以降低耦合

### 2026-01-20 22:20 - 简化事件映射构建

- **修改文件**:
  - `stage_2/src/DataCrawler/ResultQueryService.java`
- **修改内容**:
  - buildEventIdMap 直接返回爬虫映射结果

### 2026-01-20 22:16 - 结果查询移除大小写归一化

- **修改文件**:
  - `stage_2/src/DataCrawler/ResultQueryService.java`
- **修改内容**:
  - 不再对项目名做小写归一化，按原始 key 匹配

### 2026-01-20 22:03 - 结果映射下沉到爬虫层

- **修改文件**:
  - `stage_2/src/DataCrawler/Result/ResultCrawler.java`
  - `stage_2/src/DataCrawler/ResultQueryService.java`
- **修改内容**:
  - 将 summary 解析与 eventId 映射逻辑迁移到 ResultCrawler
  - 门面层仅做映射归一化与调用

### 2026-01-20 21:37 - 精简防御性分支

- **修改文件**:
  - `stage_2/src/DataCrawler/AthleteQueryService.java`
  - `stage_2/src/DataCrawler/ResultQueryService.java`
  - `stage_2/src/Scheduler/TaskScheduler.java`
- **修改内容**:
  - 移除多余的空值与边界校验，简化调度与查询流程
  - 运行时异常改为直接抛出

### 2026-01-20 21:12 - 新增查询门面与调度器解耦

- **修改文件**:
  - `stage_2/src/DataCrawler/QueryService.java` - 统一查询接口
  - `stage_2/src/DataCrawler/AthleteQueryService.java` - 运动员查询实现
  - `stage_2/src/DataCrawler/ResultQueryService.java` - 结果查询实现（含懒加载映射）
  - `stage_2/src/Scheduler/TaskScheduler.java` - 使用门面接口执行查询
- **修改内容**:
  - 引入统一查询门面，调度器只按项目名与 option 获取格式化输出
  - 结果查询内部维护 event 映射与详情缓存

### 2026-01-20 20:39 - 调度器运行时失败统一输出 Error

- **修改文件**:
  - `stage_2/src/Scheduler/TaskScheduler.java`
- **修改内容**:
  - result 映射缺失时不再输出 N/A，统一按运行时错误处理

### 2026-01-20 20:26 - 调度器事件缺失处理

- **修改文件**:
  - `stage_2/src/Scheduler/TaskScheduler.java`
- **修改内容**:
  - summary 获取失败时统一输出 Error

### 2026-01-20 20:25 - 调度器分组执行与事件映射

- **修改文件**:
  - `stage_2/src/Scheduler/TaskGroup.java` - 增加任务列表访问
  - `stage_2/src/Scheduler/TaskScheduler.java` - 多线程分组执行与写回输出
  - `stage_2/src/DataCrawler/Result/ResultCrawler.java` - 放开按 eventId 抓取接口
- **修改内容**:
  - TaskGroup 提供任务列表以便调度执行
  - 调度器按组启动线程、抓取一次并回填输出数组
  - 结果抓取支持 eventId 映射路径

### 2026-01-20 15:54 - 新增多线程调度说明文档

- **修改文件**:
  - `stage_2/THREADING.md`
- **修改内容**:
  - 记录无缓存场景下的多线程分组执行框架与输出顺序策略

### 2026-01-20 15:56 - 多线程说明文档改为中文

- **修改文件**:
  - `stage_2/THREADING.md`
- **修改内容**:
  - 将多线程执行框架说明改为中文版本

### 2026-01-20 15:59 - 多线程说明更新为手动线程与单次 summary

- **修改文件**:
  - `stage_2/THREADING.md`
- **修改内容**:
  - 改为每组一个线程，主线程 join 等待
  - 增加 result summary 只抓取一次并按 eventId 分组的说明

### 2026-01-20 16:03 - 多线程示例补充男子项目

- **修改文件**:
  - `stage_2/THREADING.md`
- **修改内容**:
  - 示例中新增男子项目命令与分组

### 2026-01-20 15:49 - 补充多线程调度框架说明

- **修改文件**:
  - `AI_PROJECT_CONTEXT.md`
- **修改内容**:
  - 新增调度器的多线程执行框架说明与分组示例

### 2026-01-20 15:43 - CoreModule 增加命令到任务映射

- **修改文件**:
  - `stage_2/src/Core/CoreModule.java` - 添加 Task 映射构建方法
- **修改内容**:
  - 在 CoreModule 中构建 Task 列表（players/result → key/option）
  - 保留即时输出处理，未实现任务执行

### 2026-01-20 15:26 - 命令即时输出与模块清理

- **修改文件**:
  - `stage_2/src/Command/Command.java` - 添加即时输出钩子
  - `stage_2/src/Command/ErrorCommand.java` - 直接返回 Error 输出
  - `stage_2/src/Command/ResultCommand.java` - 对 N/A 返回即时输出
  - `stage_2/src/Core/CoreModule.java` - 接入即时输出填充
  - 删除 `stage_2/src/Command/CommandType.java`
  - 删除 `stage_2/src/cache/CacheKey.java`
  - 删除 `stage_2/src/cache/LRUCache.java`
- **修改内容**:
  - 将 Error/N/A 的输出判断下放到命令对象
  - CoreModule 通过统一方法填充即时输出
  - 移除不再使用的命令枚举和缓存模块

### 2026-01-19 - 完成接口层和业务层实现

- **修改文件**:
  - `Common/DataFetcher.java` - 定义接口
  - `Common/OutputFormatter.java` - 定义接口
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
  - 设计混合架构：横切层（command/Scheduler/cache）+ 业务层（DataCrawler）
  - 创建空文件结构，待填充代码
- **架构决策**:
  - 对外 API 只暴露 `CoreModule.execute(String cmd)`，内部自动解析路由
  - 通过 `Common/` 包的接口连接横切层和业务层
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

- [x] 定义 `Common/DataFetcher.java` 接口
- [x] 定义 `Common/OutputFormatter.java` 接口
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
- [ ] 实现 `Scheduler/DependencyAnalyzer.java`
- [ ] 实现 `Scheduler/TaskGroup.java`
- [ ] 实现 `Scheduler/TaskScheduler.java`
- [ ] 实现 `Core/CoreModule.java`
- [ ] 实现 `DWASearch.java`
- [ ] 编写至少 10 个单元测试
- [ ] 打包成 `DWASearch.jar`

---

*最后更新: 2026-01-19*
