HuskChat 支持由社区贡献的多种语言本地化。默认语言为 [`en-gb`](https://github.com/NewNanCity/HuskChat-Remake/blob/master/common/src/main/resources/locales/en-gb.yml)（英语）。消息文件采用 [MineDown](https://github.com/Phoenix616/MineDown) 格式。

你可以通过修改插件 config.yml 文件顶层的 `language` 设置来更换预设语言。你必须将其更改为支持的语言代码之一。你可以通过查看 [locales 源文件夹](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales) 来[查看支持的语言列表](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales)。

## 贡献翻译
你可以通过提交 pull request，上传包含你语言翻译内容的 yaml 文件来贡献本地化。[默认语言文件](https://github.com/NewNanCity/HuskChat-Remake/blob/master/common/src/main/resources/locales/en-gb.yml)可作为翻译模板。注意事项如下：
* 不要翻译本地化键名（如 `channel_switched`）
* Pull request 应提交到 [locales 文件夹](https://github.com/NewNanCity/HuskChat-Remake/tree/master/common/src/main/resources/locales)
* 不要翻译 [MineDown](https://github.com/Phoenix616/MineDown) 语法本身或命令及其参数，仅翻译英文界面文本
* 每条本地化内容应为一行，且应移除文件头部注释
* 使用正确的 ISO 639-1 [语言代码](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) 作为文件名
* 如有需要，你可以自行将名字添加到 `AboutMenu` 的译者名单，否则我们也可以帮你添加

感谢你为 HuskChat 的全球化做出的贡献！
