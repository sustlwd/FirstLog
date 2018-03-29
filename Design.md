#  项目设计文档
## 项目名称
	Android系统log存储
## 项目框架
1. 项目平台
	+ 使用Android系统的TV板卡进行开发测试客户端
	+ 使用Linux平台编码运行服务器
2. client
	+ 在TV板卡上运行的一个Android应用，应用界面包含启动和完成两个按键实现启动应用抓取log和关闭应用，两个文本框输入server端的IP和Port。
	+ 启动应用client与server端建立连接后，抓取Android系统运行期间的相关log并及时上传到server端。
	+ 在client和server端进行log数据通信的同时，client端可以接收server端回复的调试命令并将命令执行结果发送回server端。
3. server
	+ 运行于linux平台上的一个并发服务器，该服务器能及时响应client的请求连接。
	+ server和client建立连接后将client发送的log数据存储在本地对应文件中。
	+ 在server终端可以输入反馈调试命令发送到client，收到client的命令执行结果显示在终端上。
## 项目核心开发流程：
	编码 -> 调试 -> 验证 -> 验收
1. 编码
	+ client编码实现获取android系统log信息，将其log按需求格式(包含时间、处理器、软件版本、log类别、log信息)封装数据包通过TCP连接发送到server端。
	+ server编码实现和client建立TCP连接，及时接收client端的log数据包并将其存储到本地文件中。
2. 调试
	+ 搭建项目系统进行调试，确认两边能够正常通信以及数据正确收发，调试中发现通信异常再继续修改完善。
3. 验证
	+ 使用多种测试用例去验证系统的准确性、稳定性，验证中发现异常再继续修改完善。
4. 验收
	+ 验证无误输出详细的项目设计文档上传项目到GitHub进入项目的验收阶段。