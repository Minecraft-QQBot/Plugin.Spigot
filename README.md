# Spigot
和 [BotServer](https://github.com/Minecraft-QQBot/BotServer) 对接的 Spigot 插件，同时也支持 Paper 和 Bukkit 服务器。


你可以到 [Releases](https://github.com/Minecraft-QQBot/Spigot/releases) 下载最新版本的服务器插件。

## 功能

- 可以播报玩家死亡。
- 可以使用 /qq 指令发送消息。

## 安装

将插件文件放入插件目录中，重启服务器。


## 配置

打开默认的插件配置文件夹，找到 `QQBot` 下的 `config.yml` 按照说明进行编辑。

```yml
uri: ws://host:port/
token: 你机器人配置的 token
name: 服务器名称
```

其中各个字段的含义如下：

|        字段名         | 类型  |                  含义                   |
|:------------------:|:---:|:-------------------------------------:|
|        uri         | 字符串 | WebSocket 连接的 Uri，格式为 ws://host:port/ |
|        name        | 字符串 |             服务器名称，中英文都可。              |
|       token        | 字符串 |      口令，和服务器配置文件下的 TOKEN 保持一致即可。      |

当你看到类似 `[Listener] 与机器人成功建立链接！` 的日志时，你的服务器已经成功连接到机器人服务器。若出现错误提示，请确保你的机器人服务器已经开启，或者配置文件的 Port 是否正确。你可以通过 `server` 指令查看服务器是否连接上机器人。

> [!TIP]
> 若插件遇到问题，或有更好的想法，可以加入 QQ 群 [`962802248`](https://qm.qq.com/q/B3kmvJl2xO) 或者提交 Issues
> 向作者反馈。若你有能力，欢迎为本项目提供代码贡献！
