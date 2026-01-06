package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.common.splitHtml
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import java.util.Date


@Composable
fun LDThread(
    entry: Entry,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth()
            .then(if (entry.isUnread) Modifier else Modifier.alpha(0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            FeedIcon(
                entry.feed.iconURL,
                entry.feed.title,
                FeedIconDefaults.LARGE.copy(radius = 99.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            entry.feed.title,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            style = AppTypography.M15,
                        )
                        if (entry.author.isNotEmpty()) {
                            Text(entry.author, maxLines = 1, style = AppTypography.M13B50)
                        }
                    }
                    Text(
                        entry.publishedAt.showTime(),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        style = AppTypography.M13B25,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                entry.content.splitHtml().forEach { segment ->
                    Text(
                        text = segment,
                        style = AppTypography.R15,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text("Read More...", maxLines = 1, style = AppTypography.R15Blue)
                Spacer(modifier = Modifier.height(8.dp))
                //图片
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLDThread() {
    val entry = Entry.EMPTY.copy(
        title = "The best last-minute Cyber Monday deals you can still shop",
        summary = "There’s still some time to save on a wide range of Verge-approved goods, including streaming services, iPads, and ebook readers.",
        feed = Feed.EMPTY.copy(title = "The Verge"),
        publishedAt = Date().time,
        content = "<p>1 月 5 日，荣耀公司通过线上新品发布会推出了荣耀 Power2 手机，并同步发布了荣耀平板 X10 Pro、荣耀平板 10 Pro 及多款亲选 IoT 产品。</p><p>其中，荣耀 Power2 采用行业首发 10080mAh 第四代青海湖电池，机身厚度控制在 7.98mm，支持 80W 超级快充与 27W 反向快充，并加入智慧充电引擎以提升充电效率与电池寿命表现。</p><p>通信方面，荣耀 Power2 搭载鸿燕六翼天线与自研射频增强芯片 C1+，支持 25 根天线，并引入平行双轨天线设计以提升弱网表现；定位采用多源融合定位引擎，支持 GPS 双频与北斗三频。性能方面首发天玑 8500 Elite，并配备超 40000mm²+ 冰封液冷散热系统；整机通过 SGS 金标五星抗跌耐摔与抗挤压认证，具备 IP68、IP69、IP69K 防尘防水，搭载 6.79 英寸 1.5K 护眼屏与 5000 万像素 OIS 主摄。</p><figure><img src=\"https://cdnfile.sspai.com/2026/01/05/article/8567112405b62409f59ca6070c90781a.jpeg\" loading=\"lazy\"><figcaption>产品核心亮点图，图片截取自荣耀</figcaption></figure><p>荣耀 Power2 提供「旭日橙」「雪原白」「幻夜黑」三色，12GB+256GB 售价 2699 元，12GB+512GB 售价 2999 元，国补优惠价分别为 2294.15 元与 2549.15 元；产品已开启预售，计划于 1 月 9 日 10:08 正式开售。<a href=\"https://weibo.com/3206603957/QlK8Jv5ow\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>1 月 5 日，杜比实验室与抖音宣布达成合作，抖音将把杜比视界带给平台用户，用于创作与分享更高动态范围的视频内容。</p><p>据杜比介绍，iPhone 用户现已可在抖音 App 内发布、观看杜比视界视频，并可使用抖音内置剪辑工具，以及剪映等兼容编辑应用制作杜比视界内容；对更多设备类型的支持将陆续推出。</p><p>杜比方面表示，短视频平台的兴起改变了人们分享与观看内容的方式，此次合作希望让创作者更便捷地在移动端使用杜比视界完成创作与分发。<a href=\"https://news.dolby.com/zh-CN-CN/259207-/\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>腾讯微信「公众平台助手小程序」于 1 月 3 日发布公告称，自 2026 年 3 月 2 日起，公众平台助手小程序将停止提供服务。官方表示，如果用户需继续在手机上管理公众号，可前往应用市场下载「公众号助手」，或在「微信」——「公众号」对公众号进行管理。<a href=\"https://m.ithome.com/html/910075.htm\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>近日，徕卡相机股份公司（Leica Camera AG）所有者兼监事会主席安德烈亚斯・考夫曼博士（Dr. Andreas Kaufmann）在播客节目中透露，徕卡目前正在自主研发全画幅 CMOS 传感器。该研发工作大约始于四年前（2022 年），即徕卡 M11 发布前后，整个研发过程预计耗时五年左右，也就是预计最快能在 2027 年看到搭载徕卡自家 CMOS 的 M12 机型。</p><p>作为比较，徕卡 M11、Q3、SL3 使用定制版索尼 IMX455 传感器；M10R、Q2、SL2 的具体传感器型号目前争议较大，但也由索尼供应；SL2-S 和 SL3-S 则使用定制版索尼 IMX410 传感器。<a href=\"https://m.ithome.com/html/910067.htm\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>智能健康品牌 Withings 在 CES 2026 中推出了 Body Scan 2 体重秤，新机型主要调整了设计，并新增了高血压风险检测和细胞健康评估等功能。</p><p>在设计层面，Body Scan 2 将原先位于秤体表面的显示屏调整到了用于测量心电图的可拉出式把手上，允许用户更轻松地查看数据信息。</p><p>传感器方面，Body Scan 2 体重秤可测量高达 60 项人体指标，其中最核心的新功能是阻抗心动描记技术（ICG），可用于评估心脏向全身器官泵血的能力，以进行高血压风险检测；另一项新增的生物电阻抗光谱分析（BIS）则通过极低强度电流检测人体总水分含量，从而推算细胞年龄、活跃细胞质量以及代谢效率等关键指标。所有的数据会交由 Withings 旗下「经临床验证」的 AI 模型进行分析，生成更具可读性的健康洞察，例如高血压风险评估、血糖调节能力分析等。同时系统还会结合用户的生命体征数据，给出健康趋势判断，并提供一系列改善健康的建议。</p><figure><img src=\"https://cdnfile.sspai.com/2026/01/05/article/eb80c0e8c4e9935a2b6dfdf14f714d08.jpeg\" loading=\"lazy\"><figcaption>产品外观图，图片来自 Withings</figcaption></figure><p>体重秤定价 599.95 美元，2026 年第二季度上市。<a href=\"https://www.ithome.com/0/910/415.htm\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>近日，任天堂公司通过 21.1.0 固件悄然解决了 Switch 2 底座以太网口的联网不稳定问题，有玩家反馈，更新后需要对机器进行一次断电重启后，以太网接口连接网络就正常了。</p><p>玩家猜测此前链接不稳定的原因是 Switch 2 的瑞昱芯片存在缺陷，但这次更新修复可能是官方为潜在的硬件问题找到了某种变通方案。<a href=\"https://www.notebookcheck.net/A-Switch-2-firmware-update-may-have-finally-fixed-Ethernet-ports-on-the-console-s-dock.1195469.0.html\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><p>近日，越南政府发布第 342 号法令（Decree 342 2025），对数字广告作出更细化规定，并将于 2026 年 2 月 15 日生效。根据该法令规定，在越南境内投放的在线视频及动态图片广告，平台必须确保用户在最长 5 秒内即可跳过或关闭，不得再提供 7 秒至 30 秒等更长时间的「不可跳过」广告形式。</p><p>新规同样覆盖静态图片广告，要求广告主不得设置「必须等待才能关闭」的机制；对弹窗等非固定位置广告，法令禁止使用难以识别或误导性的关闭按钮，用户应能通过一次清晰操作完成关闭。</p><p>此外，法令要求线上广告需提供明显图标与指引，便于用户举报违法内容，并可选择拒绝或停止观看不适当广告。对于被主管部门认定违法的广告，广告主、服务商、分发方与发布方须在接到要求后 24 小时内完成下架或清除；电信运营商与互联网服务提供商也需在收到正式请求后 24 小时内限制访问相关违法广告或服务。<a href=\"https://vietnamnet.vn/en/vietnam-to-limit-online-ad-skip-time-to-5-seconds-starting-february-15-2478911.html\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">来源</a></p><ul><li>年末「夯」一下！少数派 2025 年度盘点<a href=\"https://sspai.com/a/nOxAVJ\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\"><strong>正式上线</strong></a>。</li><li>少数派会员年终福利来袭，引荐比例限时上调至 15%，邀请好友享 85 折入会优惠。<a href=\"https://sspai.com/prime/referral-contest\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\"><strong>参与活动</strong></a></li><li>好玩又实用，还有迪士尼授权配件可选，少数派「扭扭宝」充电宝火爆开售。<a href=\"https://sspai.com/post/104627\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\"><strong>来一个试试</strong></a></li><li>GAMEBABY for iPhone 17 Pro &amp; 17 Pro Max 系列现已上市。<a href=\"https://sspai.com/post/103520\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\"><strong>进一步了解</strong></a></li><li>《蓝皮书》系列新版上架，一起探索全新 iOS 和 macOS 的精彩。<a href=\"https://sspai.com/post/103832\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\"><strong>试读并选购</strong></a></li></ul><p>&gt; 下载 <a href=\"https://sspai.com/page/client\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">少数派 2.0 客户端</a>、关注 <a href=\"https://sspai.com/s/J71e\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">少数派公众号</a>，解锁全新阅读体验 \uD83D\uDCF0</p><p>&gt; 实用、好用的 <a href=\"https://sspai.com/mall\" rel=\"noopener noreferrer\" target=\"_blank\" referrerpolicy=\"no-referrer\">正版软件</a>，少数派为你呈现 \uD83D\uDE80</p>"
    )
    Column {
        LDThread(entry)
        LDThread(entry.copy(author = "@AphexTwin"))
    }
}