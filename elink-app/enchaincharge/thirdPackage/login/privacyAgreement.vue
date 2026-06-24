<template>
	<view class="container">
		<view class="text textIndent">{{ appConfig.appletName }}用户隐私政策</view>
		<view class="text textIndent">更新日期：2022年9月15日</view>
		<view class="text textIndent">生效日期：2022年9月19日</view>
		<view class="text textIndent">为切实保护{{ appConfig.appletName }}用户隐私权益，优化用户体验，{{ appConfig.appletName }}运营方晟曼电力科技有限公司（注册地为浙江省宁波市北仑区梅山盐场1号办公楼十二号1666室，以下简称“我们”）根据现行法规及政策，制定本隐私政策。本隐私政策将详细说明我们在获取、管理及保护用户个人信息方面的政策及措施。本隐私政策适用于我们向您提供的所有服务，无论您是通过计算机设备、移动终端或其他设备获得的{{ appConfig.appletName }}服务。对于本隐私政策或相关事宜有任何问题，您可随时通过访问{{ appConfig.appletName }}在线客服系统、{{ appConfig.appletName }}官方服务账号或拨打我们的任何一部客服电话等多种方式与我们联系，我们会尽快为您作出解答。</view>
		<view class="text textIndent">本隐私政策是您使用{{ appConfig.appletName }}服务及各项功能的基础性文件，我们希望您在使用{{ appConfig.appletName }}服务前仔细阅读并明确您已经充分理解、接受本政策的内容，希望您可以根据自己的理解做出合适的选择。我们努力通过通俗的语言表述本隐私政策中涉及的相关术语，并提供进一步说明的链接，以便您更好地理解。在我们更新本隐私政策后（我们会及时提示您更新的情况）您继续使用我们的产品与/或服务，即意味着您同意本隐私政策(含更新版本)内容，并且同意我们按照本隐私政策收集、使用、保存和共享您的相关信息。</view>
		<view class="text textIndent">本隐私政策涉及的个人信息包括：基本信息（包括个人姓名、生日、性别、住址、个人电话号码、电子邮箱）；个人身份信息（包括身份证、军官证、护照、驾驶证、相关身份证明）；个人位置信息；网络身份标识信息（包括系统账号、IP地址、邮箱地址及与前述有关的密码、密保）；通讯录；个人上网记录（包括搜索记录、软件使用记录、点击记录）；个人常用设备信息（包括硬件型号、设备MAC地址、操作系统类型、软件列表唯一设备识别码（IMEI/androidID/IDFA/OPENUDID/GUID、SIM卡IMSI信息在内的描述个人常用设备基本情况的信息）。</view>
		<view class="text textIndent">个人敏感信息是指一旦泄露、非法提供或滥用可能危害人身和财产安全，极易导致个人名誉、身心健康受到损害或歧视性待遇等的个人信息，本隐私政策中涉及的个人敏感信息包括：个人身份信息（包括身份证、护照）；网络身份识别信息（包括账户名、账户昵称）；其他信息（包括个人电话号码、手机号码、行程信息、精准定位信息）；相关身份证明；请您谨慎并留意敏感个人信息，您同意我们可以按本政策所述的目的和方式来处理您的敏感个人信息。</view>
		<view class="text title">一、个人信息的收集</view>
		<view class="text textIndent">您已知悉且同意基于优化用户体验之目的，我们会获取与提升{{ appConfig.appletName }}服务有关的其他信息，例如当您访问{{ appConfig.appletName }}时，我们可能会收集哪些服务的受欢迎程度、浏览器软件信息以便优化我们的服务。</view>
		<view class="text textIndent">在您使用{{ appConfig.appletName }}前，我们会引导您阅读本协议，并在您接受本协议的基础上，获得您的相关个人信息。如果您不同意提供个人信息，您将无法使用{{ appConfig.appletName }}的全部或部分功能和服务，或无法获得更个性化的功能，或无法参加某些活动，或无法收到我们的通知等。</view>
		<view class="text">我们仅会在出现下列情况时收集您的个人信息：</view>
		<view class="text textIndent">在您注册{{ appConfig.appletName }}账号时，我们会收集您的手机号码、性别、密码、账户昵称信息。若您以其他方式关联登录{{ appConfig.appletName }}，我们会向关联第三方请求您的个人信息，对于我们要求但第三方无法提供的个人信息，我们仍需要您提供。收集、使用手机号码是为了满足相关法律法规的真实身份信息认证要求。若您不提供这类信息，您可能无法正常使用我们的服务。您还可以根据自身需求选择填写性别、地区信息。如果您拒绝提供这些信息，将影响您使用{{ appConfig.appletName }}或{{ appConfig.appletName }}的相关功能。在您注册使用{{ appConfig.appletName }}时，我们会对您进行实名验证，如果您拒绝提供有关信息，我们将拒绝向您提供服务。</view>
		<view class="text textIndent">在您发送{{ appConfig.appletName }}、使用{{ appConfig.appletName }}提供的位置定位服务时，我们会收集您的位置信息、设备信息。我们保证在您的设备使用定位服务时不会收集其他无关信息。</view>
		<view class="text textIndent">在您使用我们提供的搜索服务时，我们会收集您的查询关键字信息、设备信息。为了提供高效的搜索服务，这些信息有部分会暂时存储在您的本地存储设备之中。在此，您需要注意的是，您的关键词信息无法单独识别您的个人身份，其不属于您的个人信息，因此我们有权以其他的目的对其进行使用；只有当您的搜索关键词信息与您的其他信息互有联系并可以识别您的个人身份时，则在结合使用期间，我们会将您的搜索关键词信息作为您的个人信息，与您的搜索历史记录一同按照本隐私政策对其进行处理与保护。</view>
		<view class="text textIndent">当您参加{{ appConfig.appletName }}的有关营销活动时，我们会收集您的姓名、学校名称、通讯地址、联系方式、银行账号信息。这些信息是您收到转账或礼品的基础，如果您拒绝提供这些信息，我们将无法向您转账或发放礼品。</view>
		<view class="text textIndent">我们会收集您设备的具体位置信息为您提供您所在城市的有关资讯，我们所收集的信息中不会涉及您设备的其他信息。</view>
		<view class="text textIndent">我们在经过您同意后获取您设备的相机权限用于您在扫描充电桩的二维码时启动充电。</view>
		<view class="text textIndent">当您在{{ appConfig.appletName }}中使用第三方提供的服务时，您有权决定是否允许第三方收集您的订单信息、支付信息，如果您拒绝第三方在提供服务时收集此类信息，将可能会导致您无法在{{ appConfig.appletName }}中使用第三方服务。</view>
		<view class="text textIndent">友盟推送SDK，为了向您及时推送服务通知，我们使用了友盟推送SDK，该SDK需要获取您的存储权限、手机终端唯一标志信息（IMEI号）、手机型号、系统类型、系统版本、网络类型、SIM卡运营商信息、设备App列表信息、MAC地址、国际移动用户识别码（IMSI）、正在运行程序，用于实现App推送服务。</view>
		<view class="text textIndent">高德地图SDK高德软件有限公司点击查看协议定位，导航，提供服务网点信息，位置选择“位置信息”，包括GNSS信息、IP地址、WLAN热点、基站信息；“设备信息”，包括终端设备唯一标识符（包含IDFV、OAID、IMEI、MEID、IMSI）、mac地址、操作系统版本、设备品牌及型号、设备配置、运营商类型、应用信息（应用名、应用版本号）基于对您信息的有效保护并且在您同意的基础上，我们会对收集到的您的信息进行去标签化处理，以保护您的信息安全。在此希望您了解并接受在不透露您个人信息的前提下，我们有权对您的信息进行分析并予以商业化的利用。</view>
		<view class="text">对Cookie及web beacon的使用</view>
		<view class="text">Cookie可以帮助网站辨认注册用户，计算用户数量，通常被各网站用来判定完成注册的用户是否已经实现登录。我们承诺，对cookie信息的研究仅用于提升服务/产品质量及优化用户体验之目的。同时，如不希望个人信息保留在cookie中，您可以对浏览器进行配置：选择"拒绝cookie"或"当网站发送cookie时通知您"，您知道，鉴于{{ appConfig.appletName }}的服务是通过支持cookie来是实现的，完成关闭cookie的操作后，可能影响到您访问{{ appConfig.appletName }}或不能充分取得{{ appConfig.appletName }}的服务。您不禁用cookie时，可能会得到提示，是否在下一次进入此网站时保留用户信息以便简化登录手续（如一键登录）。</view>
		<view class="text">我们使用webbeacon的方法和目的与cookie是相同的。</view>
		<view class="text">为了向您提供更好的服务或产品，我们会在下述情形使用您的个人信息：</view>
		<view class="text textIndent">1.根据相关法律法规的要求；</view>
		<view class="text textIndent">2.根据您的授权；</view>
		<view class="text textIndent">3.根据{{ appConfig.appletName }}相关服务条款、应用许可使用协议的约定。</view>
		<view class="text">此外，您已知悉并同意：在现行法律法规允许的范围内，我们可能会将您非敏感的个人信息用于市场营销，使用方式包括但不限于：我们会使用您所提供信息在{{ appConfig.appletName }}平台中向您展示或提供广告和促销资料，向您通告或推荐{{ appConfig.appletName }}的服务或产品信息，以及其他此类根据您使用{{ appConfig.appletName }}服务或产品的情况所认为您可能会感兴趣的信息。其中也包括您在采取授权等某动作时选择分享的信息，例如当您新增好友、在动态中新增地标、使用{{ appConfig.appletName }}的联络人汇入工具等。</view>
		<view class="text">个人信息的保护和安全措施</view>
		<view class="text">我们将尽一切合理努力保护其获得的用户个人信息，并由专门的数据安全部门对个人信息进行保护。为防止用户个人信息在意外的、未经授权的情况下被非法访问、复制、修改、传送、遗失、破坏、处理或使用，我们已经并将继续采取以下措施保护您的个人信息：</view>
		<view class="text">1、我们采取的安全措施</view>
		<view class="text textIndent">1）我们的网络服务采取了传输层安全协议等加密技术，通过https等方式提供浏览服务，确保用户数据在传输过程中的安全。</view>
		<view class="text textIndent">2）通过采取加密技术对用户个人信息进行加密保存，并通过隔离技术进行隔离。</view>
		<view class="text textIndent">3）在个人信息使用时，例如个人信息展示、个人信息关联计算，我们会采用包括内容替换、加密脱敏等多种数据脱敏技术增强个人信息在使用中安全性。</view>
		<view class="text textIndent">4）设立严格的数据使用和访问制度，采用严格的数据访问权限控制和多重身份认证技术保护个人信息，避免数据被违规使用。</view>
		<view class="text textIndent">5）采取专门的数据和技术安全审计，设立日志审计和行为审计多项措施。</view>
		<view class="text">2、保护个人信息采取的其他安全措施</view>
		<view class="text textIndent">1）通过建立数据分类分级制度、数据安全管理规范、数据安全开发规范来管理规范个人信息的存储和使用。</view>
		<view class="text textIndent">2）建立数据安全专项部门，负责安全应急响应组织来推进和保障个人信息安全。</view>
		<view class="text textIndent">3）加强安全意识。我们还会举办安全和隐私保护培训课程，加强员工对于保护个人信息重要性的认识。</view>
		<view class="text">3、个人信息安全事件的通知</view>
		<view class="text textIndent">1）如发生个人信息引发的安全事件，我们将第一事件向相应主管机关报备，并即时进行问题排查，开展应急措施。</view>
		<view class="text textIndent">2）通过与全量用户发送通知提醒更改密码。还可能通过电话、短信等各种方式触达用户知晓，在{{ appConfig.appletName }}公共运营平台运营宣传，制止数据泄露。尽管已经采取了上述合理有效措施，并已经遵守了相关法律规定要求的标准，但我们仍然无法保证您的个人信息通过不安全途径进行交流时的安全性。因此，用户个人应采取积极措施保证个人信息的安全，如：定期修改账号密码，不将自己的账号密码等个人信息透露给他人。</view>
		<view class="text">您知悉：我们提供的个人信息保护措施仅适用于{{ appConfig.appletName }}平台，一旦您离开{{ appConfig.appletName }}，浏览或使用其他网站、服务及内容资源，我们即没有能力及义务保护您在{{ appConfig.appletName }}以外的网站提交的任何个人信息，无论您登录或浏览上述网站是否基于{{ appConfig.appletName }}的链接或引导。</view>
		<view class="text">网络环境中始终存在各种信息泄漏的风险，当出现意外事件、不可抗力等情形导致您的信息出现泄漏时，我们将极力控制局面，及时告知您事件起因、我们采取的安全措施、您可以主动采取的安全措施等相关情况。</view>
		<view class="text">个人信息的存储</view>
		<view class="text textIndent">我们会采取合适的安全措施和技术手段存储及保护您的个人信息，以防止丢失、被误用、受到未授权访问或泄漏、被篡改或毁坏。您的个人信息存放在有密码控制的位于中国境内的服务器中，访问均是受到限制的。当您需要跨境信息传输服务时，我们会默认您继续使用中国境内的服务器。</view>
		<view class="text">我们会依照个人信息的不同等级存储不同期限，存储期限严格按照法律及相关法规规定，最低期限不少于6个月。 在超出保留期间后，我们会根据适用法律的要求删除您的个人信息，或将其匿名化处理。</view>
		<view class="text">根据本条款的规定，我们仅允许有必要知晓这些信息的我们员工等第三方访问个人信息，并要求他们履行相应的保密义务。</view>
		<view class="text">个人信息的使用和对外提供</view>
		<view class="text">未经您本人允许，我们不会向任何第三方公开（包括共享、转让、公开披露等方式）您的个人信息，下列情形除外：</view>
		<view class="text textIndent">1)我们已经取得您或您监护人的授权或同意；</view>
		<view class="text textIndent">2)司法机关或行政机关基于法定程序要求我们披露的；</view>
		<view class="text textIndent">3)我们为维护自身合法权益而向用户提起诉讼或仲裁时；</view>
		<view class="text textIndent">4)根据您与{{ appConfig.appletName }}相关服务条款、应用许可使用协议的约定；</view>
		<view class="text textIndent">5)在法律允许的范围内，为保障{{ appConfig.appletName }}、{{ appConfig.appletName }}用户以及社会公共利益免受损害时；</view>
		<view class="text textIndent">6)符合您与其他第三人之间有关约定的；</view>
		<view class="text">您可以向特定的对象发送您的照片、身份信息、位置信息，也可以基于{{ appConfig.appletName }}的服务向不特定人共享自己的照片、位置信息、身份信息。当您因为您的共享行为导致您的信息发生泄漏等严重情况时，我们不承担相关法律责任。</view>
		<view class="text">随着我们业务的持续发展，我们有可能进行合并、分立等交易，若涉及个人信息转移的，我们将告知你相关情形，按照法律法规及不低于本指引所要求的标准继续保护或要求新的处理者继续保护你的个人信息。</view>
		<view class="text">1.委托处理</view>
		<view class="text textIndent">为了更好的为您提供服务，我们可能委托第三方处理部分个人信息。</view>
		<view class="text textIndent">请您放心，我们将采取受托方安全能力评估等措施确保受托方仅在约定的目的和范围内处理您的个人信息，并保障您的个人信息安全。除此之外，我们未委托其他第三方公司、组织或个人处理您的个人信息。</view>
		<view class="text">2.共享</view>
		<view class="text">我们不会与任何公司、组织或个人分享您的个人信息，但以下情况除外：</view>
		<view class="text textIndent">1）事先获得您的明确同意的；</view>
		<view class="text textIndent">2）根据法律法规规定，或者政府主管部门、司法机关的强制要求必须对外共享的；</view>
		<view class="text textIndent">3）为了实现本政策所述的某些业务功能和目的，我们可能会与特定的合作伙伴共享您的一些个人信息，具体如下：</view>
		
		<uni-table border emptyText="暂无更多数据" >
			<!-- 表头行 -->
			<uni-tr>
				<uni-th align="center">序号</uni-th>
				<uni-th align="center">合作伙伴类型</uni-th>
				<uni-th align="center">共享个人信息的目的</uni-th>
				<uni-th align="center">涉及个人信息类型</uni-th>
			</uni-tr>
			<uni-tr>
				<uni-td align="center">1</uni-td>
				<uni-td align="center">产品或服务提供商</uni-td>
				<uni-td align="center">实现产品或服务的购买、售后服务</uni-td>
				<uni-td align="center">产品或服务信息、交易信息、联系信息</uni-td>
			</uni-tr>
			<uni-tr>
				<uni-td align="center">2</uni-td>
				<uni-td align="center">银行或第三方支付机构</uni-td>
				<uni-td align="center">实现支付</uni-td>
				<uni-td align="center">支付金额、支付方、支付方式、支付时间、收付款主体</uni-td>
			</uni-tr>
			<uni-tr>
				<uni-td align="center">3</uni-td>
				<uni-td align="center">广告、媒体运营商</uni-td>
				<uni-td align="center">广告投放和提升营销效果</uni-td>
				<uni-td align="center">统计信息</uni-td>
			</uni-tr>
		</uni-table>
		
		<view class="text">对于我们与之共享个人信息的机构，我们会与其签署严格的保密协议/条款，要求其按照我们的要求采取相关的保密和安全措施来处理我们共享的个人信息。在涉及个人敏感信息时，我们要求该等机构采用脱敏和加密技术对个人信息进行保护，从而更好地保护您的个人信息。</view>
		<view class="text">3.转让</view>
		<view class="text">除以下情形外，我们不会将您的个人信息转让给任何公司、组织或个人：</view>
		<view class="text textIndent">1）事先获得您的明确同意的；</view>
		<view class="text textIndent">2）根据法律法规规定，或者政府主管部门、司法机关的强制要求必须对外转让的；</view>
		<view class="text textIndent">3）涉及公司合并、分立、解散、资产或业务转让、破产清算或类似的交易时，若涉及到您的个人信息转让，我们会要求新持有您个人信息的公司、组织或个人继续受本政策的约束，否则我们将要求该等公司、组织或个人重新取得您的授权同意。</view>
		<view class="text">4.公开披露</view>
		<view class="text">除以下情形外，我们不会公开披露您的个人信息：</view>
		<view class="text textIndent">1）事先获得您的明确同意的；</view>
		<view class="text textIndent">2）根据法律法规规定，或者政府主管部门、司法机关的强制要求必须公开披露的。在符合法律法规的前提下，当我们收到上述披露信息的请求时，我们会要求必须出具与之相应的法律文件，如传票或调查函。我们坚信，对于要求我们提供的信息，应该在法律允许的范围内尽可能保持透明。我们对所有的请求都将进行慎重的审查，以确保其具备合法依据，且仅限于执法部门因特定调查目的且有合法权利获取的数据。</view>
		<view class="text">您的权利</view>
		<view class="text">根据国家相关法律、法规、标准，我们保障您对自己的个人信息行使以下权利：</view>
		<view class="text">当您完成{{ appConfig.appletName }}的账号注册并进行合理和必要的身份验证后，您可以查阅、修改、删除您提交给我们的个人信息。一般情况下，您可随时浏览、修改、删除自己提交的信息，但出于安全性和身份识别（如号码申诉服务）的考虑，您可能无法修改注册时提供的某些初始注册信息及验证信息。 您可以在登录{{ appConfig.appletName }}APP后，在“个人中心”进行个人信息设置，我们为用户提供个性化的用户昵称设置功能，支持查询个人信息及设置头像等。 我们始终将对您个人信息的保护放在首位，同时，我们重视您对个人信息的关注。对于您的个人信息，您可以通过手机客户端等途径在线查询、修改、删除您的个人信息。</view>
		<view class="text">您有权自主更新或更正您的个人信息，或授权{{ appConfig.appletName }}客服进行信息更新、更正。在您进行信息更新更正之前，我们会首先验证您的身份，其次才能进行信息的更正与更新。如果您对本隐私政策或其中有关您个人信息的收集、使用、存储、保护等功能存在意见或建议时，您可以通过{{ appConfig.appletName }}功能页面、或{{ appConfig.appletName }}客户服务渠道反馈意见或投诉，我们会在收到您意见及建议的第一时间将反馈信息回复给您。</view>
		<view class="text">如果您需要撤回授权个人信息，可在您的设备-管理-应用-权限设置中进行禁止操作。您可以通过关闭{{ appConfig.appletName }}部分功能的方式阻止我们获得您的个人信息，关闭部分功能之后，因您的某些授权是实现我们某些业务功能的必要权限，您撤回相应授权可能会影响您使用我们对应的部分业务功能。当您收回同意后，我们将不再处理相应的个人信息，但您收回同意的决定，不会影响此前基于您的授权而开展的个人信息处理活动。</view>
		<view class="text">您有权获取您的个人信息副本，可通过发送电子邮件的方式向我们提出申请。</view>
		<view class="text">在技术可行的情况下，如数据接口已匹配，我们还可按照您的要求，直接将您的个人信息副本传输给您指定的第三方。</view>
		<view class="text">在某些业务功能中，我们可能仅依据信息系统、算法在内的非人工自动决策机制作出决定。如果这些决定显著影响您的合法权益，您有权要求我们作出解释，我们也将提供适当的救济方式。</view>
		<view class="text">您可以自主要求注销{{ appConfig.appletName }}账号以保障我们不再收集您的个人信息。您可以通过手机客户端注销账号，在您注销您的账号之前，我们将验证您的个人身份、安全状态、常用设备等信息。您注销账号的行为是不可逆的行为，一旦您注销您的账号，我们将即刻删除有关您账户的一切信息，并保证这些信息不会泄露，同时，您也无法通过已注销的账户获得{{ appConfig.appletName }}相关产品的服务。</view>
		<view class="text">为了确保上述请求的提出系您本人操作，保障您的个人信息安全，我们会对您的身份进行验证（必要时可能需要您提供书面请求），在身份验证通过后，再处理您的请求。</view>
		<view class="text">对于您所提出的上述请求，我们将在15个工作日内核实和处理。</view>
		<view class="text">对于您合理的请求，我们原则上不收取费用，但对于多次重复、超出合理限度的请求，我们将视情况收取一定的成本费用。对于那些无端重复、需要过多技术手段（例如，需要开发新系统或从根本上改变现行惯例）、给他人合法权益带来风险或者非常不切实际（例如，涉及备份磁带上存放的信息）的请求，我们可能会予以拒绝。</view>
		<view class="text">如果我们终止服务或运营，我们会至少提前三十日向您通知，并在终止服务或运营后对您的个人信息进行删除或匿名化处理。</view>
		<view class="text">请您知悉，在以下情形中，我们将无法响应您的请求：</view>
		<view class="text textIndent">1）与我们履行法律法规规定的义务相关的；</view>
		<view class="text textIndent">2）与国家安全、国防安全直接相关的；</view>
		<view class="text textIndent">3）与公共安全、公共卫生、重大公共利益直接相关的；</view>
		<view class="text textIndent">4）与刑事侦查、起诉、司法调查、审判和执行判决等直接相关的；</view>
		<view class="text textIndent">5）有充分证据表明您存在主观恶意或滥用权利的；</view>
		<view class="text textIndent">6）出于维护您或者其他个人的生命、财产等重大合法权益但又很难得到本人同意的；</view>
		<view class="text textIndent">7）响应您的请求将导致您或者其他个人、组织的合法权益收到严重损害的；</view>
		<view class="text textIndent">8）涉及商业秘密的；</view>
		<view class="text">对未成年人个人信息的特别保护</view>
		<view class="text">我们非常重视未成年人的个人信息保护，我们的产品和服务均面向成年人，不以未成年人为目标客户。我们不会故意收集任何未成年人的个人信息，除非您主动提供的某些信息中不可避免的包含了某些未成年人的信息。</view>
		<view class="text">如果您是未成年人，请不要尝试注册{{ appConfig.appletName }}，也不要以任何方式向我们提供您的任何个人信息。</view>
		<view class="text">如果我们发现自己在未事先获得可证实的监护人同意的情况下收集了任何未成年人的个人信息，我们会设法尽快删除该等信息。</view>
		<view class="text">我们非常重视对未成年人个人信息的保护。若您是14周岁以下的未成年人，在使用我们的服务前，应确保事先取得监护人的同意，如果您是已满14不满18岁的未成年人，在使用我们服务前，应确保事先取得本人或监护人的同意。我们将根据国家相关法律法规及本《个人信息保护政策》的规定保护未成年人的个人信息。</view>
		<view class="text">我们非常重视对未成年人个人信息的保护。根据相关法律法规的规定，若你是18周岁以下的未成年人，在使用{{ appConfig.appletName }}服务前，应事先取得你的家长或法定监护人的书面同意。若你是未成年人的监护人，当你对你所监护的未成年人的个人信息有相关疑问时，请通过本隐私政策中的联系方式与我们联系。</view>
		<view class="text">特别地，若你是14周岁以下的儿童，我们还专门为你制定了《儿童隐私保护声明》，儿童及其监护人在为14周岁以下的儿童完成帐号注册前，还应仔细阅读我们专门制定的《儿童隐私保护声明》。只有在取得监护人对《儿童隐私保护声明》的同意后，14周岁以下的儿童方可使用{{ appConfig.appletName }}服务。</view>
		<view class="text">个人信息保护政策的更新</view>
		<view class="text">为给您提供更好的服务以及随着我们业务的发展，本隐私政策也会随之更新。但未经您明确同意，我们不会削减您依据当前生效个人信息保护政策所应享有的权利。我们会通过在网站、移动端上发出更新版本并在生效前通过网站公告或以其他适当方式提醒您相关内容的更新，以便及时了解最新的个人信息保护政策。</view>
		<view class="text">对于重大变更，我们还会提供更为显著的通知（我们会通过包括但不限于邮件、短信、私信或在浏览页面做特别提示等方式，说明个人信息保护政策的具体变更内容）。	</view>
		<view class="text">并在本政策更新后您首次登陆时再次获取您的明示同意。若您在本政策更新且接到我们的通知后，点击同意或下一步，这表示您已充分阅读、理解并接受修订后的本政策。</view>
		<view class="text">本政策所指的重大变更包括但不限于：</view>
		<view class="text textIndent">1）我们的服务模式发生重大变化。如处理个人信息的目的、处理的个人信息类型、个人信息的使用方式等；</view>
		<view class="text textIndent">2）我们在所有权结构、组织架构等方面发生重大变化。如业务调整、破产并购等引起的所有者变更等；</view>
		<view class="text textIndent">3）个人信息共享、转让或公开披露的主要对象发生变化；</view>
		<view class="text textIndent">4）您参与个人信息处理方面的权利及其行使方式发生重大变化；</view>
		<view class="text textIndent">5）我们负责处理个人信息安全的责任部门、联络方式及投诉渠道发生变化时；</view>
		<view class="text textIndent">6）个人信息安全影响评估报告表明存在高风险时。</view>
		<view class="text">如何联系我们</view>
		<view class="text">如果您对本隐私政策有任何疑问，您可以通过以下方式与我们联系</view>
		<view class="text textIndent">1）您可以联系{{ appConfig.appletName }}客服咨询（联系电话：{{ appConfig.phone }}）</view>
		<view class="text">2）通过我司个人信息保护小组专项邮箱（shengman@sunmaxxpower.com）发送邮件的方式进行联系，我们会及时解决您的问题。</view>
		<view class="text">3）邮寄信件到如下地址：浙江省杭州市西湖区西园八路1号浙大森林E2幢503室 晟曼电力（收） 邮编518129</view>
		<view class="text">一般情况下，我们将在十五个工作日内回复。如果您对我们的回复不满意，特别是我们的个人信息处理行为损害了您的合法权益，您还可以向网信、电信、公安及工商等监管部门进行投诉或举报，或通过向被告住所地有管辖权的法院提起诉讼来寻求解决方案。</view>
		<view class="text"></view>
		
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	name: 'privacyAgreement',
	computed: {
		...mapState(['appConfig'])
	},
	data() {
		return {
			richTextNodes:""
		};
	},
	methods: {}
};
</script> 

<style scoped lang="scss">
.container { 
	width: 100%;
	background-color: #ffffff;
	padding: 10rpx 20rpx 100rpx 20rpx;
	box-sizing: border-box;
	
	.text{
		color: #242424;
		font-size: 24rpx;
	}
	
	.textIndent{
		text-indent:2em;
	}
	
	/deep/ .uni-table{
		height: 300rpx;
	}
}
</style>
