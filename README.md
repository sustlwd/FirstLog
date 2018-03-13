# FirstLog
CVTE Trainee project
## android log 存储
### 项目需求分析
1. 串口转网口的数据传输（模块）
	+ 改动系统源码，从串口输出log信息。
2. 在client定时抓取android系统运行期间的log信息。
	+ log中包含处理器、系统软件版本、log信息分类、时间节点。
3. log数据封装发送
	+ log数据封装。
	+ 网络数据传输选择TCP传输。
	+ client先发送请求连接，server端应答后开始发送log。
	+ 校验log数据传输准确无误后将其存储分析，否则丢弃。
4. 服务器端UI设计
	+ 显示log。
	+ 选择log存储路径，存储在本地一个文件中。
	+ 分析解决方案。
5. 回馈调试信息
### 项目功能描述
+ client抓取android系统运行期间的log，将log上传到服务器端，服务器端存储分析log并给予回馈调试。