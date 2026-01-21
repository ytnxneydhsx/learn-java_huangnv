# DWASearch - 爬虫开发进度与数据抓取过程

目前本项目已完成核心数据的分析与爬虫模块的初步实现。以下是关于如何通过分析官网 API 实现自动化数据抓取的详细过程。

## 数据抓取过程分析

### 1. 寻找数据源 (F12 分析)
在开发初期，我们通过访问世界泳联官网的[跳水项目结果页](https://www.worldaquatics.com/competitions/3337/66th-international-divers-day-rostock/results)，使用浏览器开发者工具（F12）的 **Network (网络)** 面板进行抓包分析。

在 **XHR/Fetch** 过滤项中，我们发现了页面加载时调用的核心接口。

### 2. 获取赛事资源清单
通过 F12 面板，我们定位到了获取该次锦标赛所有项目分类及资源 ID 的核心接口。
- **API 地址**: `https://api.worldaquatics.com/fina/competitions/3337/events`
- **过程**: `events_api_discovery.png` 展示了在 Result 首页通过 JS 调用发现该 API 网址的过程。

![发现接口地址](Img/events_api_discovery.png)

直接访问该 API 网址，我们可以得到完整的 JSON 信息（如 `events_list_json.png` 所示）。在该数据中，每个项目（如 `Women 1m Springboard`）都对应一个唯一的 `Id`，这是后续抓取详细成绩的关键钥匙。

![接口返回的 JSON 列表](Img/events_list_json.png)

### 3. 定位具体比赛项目详情
当我们在网页上点击具体的比赛项目时，JS 会携带第一步中获得的资源 ID，发起第二次 API 请求。
- **详情接口**: `https://api.worldaquatics.com/fina/events/{EventId}`
- **过程**: `event_details_api_discovery.png` 记录了点击项目后触发的第二次 JS 调用及其对应的详情 API 地址。

![点击项目触发二次调用](Img/event_details_api_discovery.png)

访问该详情 API，即可获得如 `event_details_json.png` 所示的详细比赛数据。该 JSON 结构包含了所有的 `Heats`（初赛、半决赛、决赛）以及选手的每一跳得分、总分和最终排名。

![详细比赛成绩 JSON](Img/event_details_json.png)

### 4. 获取所有运动员信息
与赛事结果不同，运动员信息的获取更为直接。通过分析运动员列表页的 XHR 请求（如 `athletes_list_xhr.png` 所示），我们发现服务器会一次性返回参与该次锦标赛的所有运动员及其所属国家的结构化 JSON 数据。
- **运动员接口**: `https://api.worldaquatics.com/fina/competitions/3337/athletes?gender=&countryId=`
- **特点**: 无需像赛事结果那样进行二次跳转或动态加载，单次请求即可获取完整名录，这极大地方便了数据的同步与持久化。

![运动员信息一次性抓取](Img/athletes_list_xhr.png)

### 5. 爬虫代码实现逻辑
基于上述逆向分析出的请求逻辑，我在 `DataCrawler` 模块中实现了对应的自动化抓取流程：
1. **赛事数据抓取**: `ResultCrawler` 采用“先匹配 ID，再获取详情”的策略。
2. **运动员数据抓取**: `AthleteCrawler` 采用“单次全量抓取”策略。
3. **本地化存储**: 所有抓取到的原始数据都会被持久化存储在本地 `data/` 目录中。

---

## 模块组织与类设计

本项目代码主要组织在 `DataCrawler` 及其子包中，遵循高内聚低耦合的设计原则。

### 1. Athlete 软件包 (运动员模块)
专门负责处理运动员相关信息的获取与解析。
- **`AthleteCrawler`**: 负责网络通信，向 API 发起 GET 请求并获取原始 JSON 字符串，支持将数据保存至本地。
- **`AthleteFormatter`**: 数据解析核心，使用 GSON 库提取运动员的姓名、性别和国籍，并严格按照作业要求的格式进行字符串拼接。
- **`AthleteService`**: 业务逻辑层，封装了“抓取+格式化”的完整流程，为上层应用提供简单的调用接口。

### 2. Result 软件包 (赛事结果模块)
负责处理复杂的赛事列表及具体比赛成绩。
- **`ResultCrawler`**: 实现了双步抓取逻辑。它能根据项目名称自动匹配 `EventId`，并进一步抓取包含得分细节的详情数据。
- **`ResultFormatter`**: 逻辑最为复杂的类。它需要处理双人项目排序、多阶段（初赛、半决赛、决赛）数据对齐、以及将每一跳的得分格式化为累加公式。
- **`ResultService`**: 业务逻辑层，提供按需获取“决赛结果”或“详细全过程结果”的服务。

---

## 当前进度
- [x] 官网 API 抓包分析与数据源定位。
- [x] 核心爬虫模块 `AthleteCrawler` 与 `ResultCrawler` 实现。
- [x] 基础数据格式化模块 `ResultFormatter` 开发。
- [ ] 命令行主程序 `DWASearch.java` 逻辑集成。

---
**作业链接**: [2024年福州大学软件工程实践第二次作业](https://bbs.csdn.net/topics/618087255)
