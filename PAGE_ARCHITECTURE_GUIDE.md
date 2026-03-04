# 页面契约与分层规范（讨论整理）

## 1. 页面六件套定义

### Args
- 含义：页面配置参数（控制展示/交互策略）。
- 示例：`dragMode`、`backAction`、`bottomButtonsLayout`。
- 约束：`Args` 不承载查询数据，不承载数据库/网络结果。

### Action
- 含义：用户业务意图（页面发给 Model）。
- 示例：`ApplyChanges`、`Unsubscribe`。
- 约束：只保留业务动作，不把纯输入事件（如每次输入字符）都塞进 `Action`。

### State
- 含义：页面状态数据（包含用户输入态 + 流程态）。
- 示例：`title`、`category`、`isApplying`、`isUnsubscribing`。
- 约束：可包含用于业务操作的关键字段（如 `feed`），并可内聚计算逻辑（如 `isModified`）。

### Effect
- 含义：一次性事件（页面副作用）。
- 示例：跳转、关闭 Sheet、错误提示。
- 约束：Model 发 Effect，页面消费 Effect。

### Model
- 含义：业务动作执行器与流程编排器。
- 约束：
  - 接收 `Action`，执行异步任务。
  - 可以修改同一份 `State` 的流程字段（如 loading）。
  - 不直接操作具体 UI 组件（例如不直接调页面控件）。

### Unit
- 含义：查询数据（Query Result），用于页面展示。
- 约束（硬性）：
  - `Unit` 始终是“查询结果 -> 页面”的方向。
  - `Unit` 绝不能从 Screen 回传给 Model。
  - 上一页面传入的业务对象不算查询数据，不应强行塞进 Unit。

---

## 2. 关键边界（最终结论）

1. `Screen` 负责发 `Action`，不在按钮回调里自行开协程做业务流程编排。  
2. `Model` 使用自身作用域执行异步任务，并在执行期间更新 `State` 的流程字段。  
3. `Model` 通过 `Effect` 通知页面做一次性处理（导航、提示）。  
4. `State` 是页面数据中心；是否修改等判断逻辑应尽量内聚到 `State`。  
5. `Unit` 只承载查询数据，严格单向流向 Screen。  
6. 即使某一类契约当前为空，也保留契约定义（保持结构一致）。  

---

## 3. EditFeed 页面落地约定

### 已确认的模式
- `EditFeedState`：持有 `feed`、`title`、`category`、`isApplying`、`isUnsubscribing`，并内聚 `isModified`。
- `EditFeedAction`：仅保留业务动作（`ApplyChanges`、`Unsubscribe`）。
- `EditFeedScreenModel`：接收同一份 `state`，执行业务动作并发 `effects`。
- `EditFeedUnit`：仅用于查询数据（如 `folders`），不从 Screen 回传。
- `EditFeedArgs`：仅页面配置。

### 反例（不要再出现）
- 在 `Screen` 的按钮点击中 `scope.launch { ...业务调用... }`。
- `Unit` 从 Screen 反向传回 Model。
- 同时维护两份等价页面状态并手工同步。

---

## 4. 推荐数据流

1. 用户操作 -> `Screen` 调 `model.onAction(...)`  
2. `Model` 启动异步 -> 更新 `state` 流程字段  
3. 页面基于 `state` 自动响应（按钮 loading 等）  
4. `Model` 发 `Effect` -> 页面执行导航/提示  

---

## 5. 简化原则

- 优先“单一真相来源”（页面状态尽量只保留一份）。
- 优先“职责清晰”而不是“层次过多”。
- 对纯 UI 输入，避免无意义的 Action 包装。

