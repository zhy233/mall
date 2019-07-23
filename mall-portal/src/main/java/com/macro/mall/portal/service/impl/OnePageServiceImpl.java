package com.macro.mall.portal.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.OnePageService;
import com.macro.mall.portal.service.RedisService;
import com.macro.mall.portal.service.UmsMemberReceiveAddressService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.HttpClientUtil;
import com.macro.mall.portal.util.HttpTool;
import com.macro.mall.portal.util.IsPayUtil;
import com.macro.mall.portal.util.UtilTools;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 单页电商营销Service实现类
 * Create by zhuyong on 2019/7/14
 */
@Service
public class OnePageServiceImpl implements OnePageService {

    @Autowired
    private PortalProductDao portalProductDao;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;

    @Autowired
    private PortalOrderDao portalOrderDao;

    @Autowired
    private OmsPortalOrderServiceImpl portalorderService;

    @Autowired
    private DmsDomainSettingMapper domainSettingMapper;

    @Autowired
    private EmsEmployeeMapper emsEmployeeMapper;

    @Autowired
    private PmsProductQrcodeMapper pmsProductQrcodeMapper;

    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper addressMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;

    @Autowired
    private PortalOrderItemDao orderItemDao;

    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;

    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;

    @Value("${pay-info.signKey}")
    private String PAY_SIGNKEY;


    @Autowired
    private RedisService redisService;
    @Value("${redis.key.prefix.orderId}")
    private String REDIS_KEY_PREFIX_ORDER_ID;

    @Value("${pay-info.merchantNo}")
    private String PAY_MERCHANTNO;
    @Value("${pay-info.notifyUrl}")
    private String PAY_NOTIFYURL;

    @Value("${is-pay.payId}")
    private Integer PAY_ID;
    @Value("${is-pay.payChannel}")
    private String PAY_CHANNEL;
    @Value("${is-pay.Notify_url}")
    private String NOTIFY_URL;
    @Value("${is-pay.payKey}")
    private String PAY_KEY;

    @Override
    public OnePageProductResult content(Long productId) {
        OnePageProductDetail productDetail = portalProductDao.getOnePageProductDetail(productId);
        OnePageProductResult result = new OnePageProductResult();
        result.setId(productDetail.getId());
        result.setName(productDetail.getName());
        result.setPic(productDetail.getPic());
        result.setSale(productDetail.getSale());
        result.setOriginalPrice(productDetail.getOriginalPrice());
        result.setStock(productDetail.getStock());
        result.setUnit(productDetail.getUnit());
        result.setPrice(productDetail.getPrice());
        result.setSubTitle(productDetail.getSubTitle());
        result.setServiceIds(productDetail.getServiceIds());
        result.setAlbumPics(productDetail.getAlbumPics());
        result.setPromotionType(productDetail.getPromotionType());
        result.setDescription(productDetail.getDescription());
        result.setDetailMobileHtml(productDetail.getDetailMobileHtml());
        result.setSkuStockList(productDetail.getSkuStockList());

        List<PmsSkuStock> skuStockList = productDetail.getSkuStockList();
        List<List<String>> spList = new ArrayList<>();
        List<String> sp1 = new ArrayList<>();
        List<String> sp2 = new ArrayList<>();
        List<String> sp3 = new ArrayList<>();
        spList.add(0, sp1);
        spList.add(1, sp2);
        spList.add(2, sp3);
        List<SkuInfo> list = new ArrayList<>();
        for (PmsSkuStock skuStock : skuStockList) {
            spList.get(0).add(skuStock.getSp1());
            spList.get(1).add(skuStock.getSp2());
            spList.get(2).add(skuStock.getSp3());

            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setId(skuStock.getId());
            skuInfo.setPrice(skuStock.getPrice());
            skuInfo.setS1(skuStock.getSp1());
            skuInfo.setS2(skuStock.getSp2());
            skuInfo.setS3(skuStock.getSp3());
            skuInfo.setStock_num(skuStock.getStock());
            list.add(skuInfo);
        }

        result.setList(list);

        PmsProductAttributeExample example = new PmsProductAttributeExample();
        example.setOrderByClause("sort desc");
        example.createCriteria().andProductAttributeCategoryIdEqualTo(productDetail.getProductAttributeCategoryId())
                .andTypeEqualTo(0);
        List<PmsProductAttribute> attributeList = productAttributeMapper.selectByExample(example);
        result.setAttributeList(attributeList);


        List<AttrTree> tree = new ArrayList<>();
        //获取商品页可选规格
        int index = 1;
        for (PmsProductAttribute attribute : attributeList) {
            AttrTree attrTree = new AttrTree();
            attrTree.setK(attribute.getName());
            String inputList = null;
            if (attribute.getInputType() == 1) {
                inputList = attribute.getInputList();
            } else if (attribute.getInputType() == 0) {
                PmsProductAttributeValueExample attributeValueExample = new PmsProductAttributeValueExample();
                attributeValueExample.createCriteria().andProductIdEqualTo(productId).andProductAttributeIdEqualTo(attribute.getId());
                List<PmsProductAttributeValue> pmsProductAttributeValues = productAttributeValueMapper.selectByExample(attributeValueExample);
                inputList = (pmsProductAttributeValues != null && pmsProductAttributeValues.size() > 0) ? pmsProductAttributeValues.get(0).getValue() : "";
            }

            String[] inputs = inputList.split(",");
            List<VNode> vNodes = new ArrayList<>();
            for (String value : inputs) {
                if (spList.get(index - 1).contains(value)) {
                    VNode vNode = new VNode();
                    vNode.setId(value);
                    vNode.setName(value);
                    vNode.setImgUrl("");
                    vNodes.add(vNode);
                }
            }
            attrTree.setV(vNodes);
            attrTree.setK_s("s" + index);
            tree.add(attrTree);
            index++;
        }

        result.setTree(tree);

        return result;
    }

    @Override
    public List<OnePageOrderDetail> getOrderDetail(String conditions) {
        List<OnePageOrderDetail> orderDetailList = new ArrayList<>();

        OmsOrderExample omsOrderExample = new OmsOrderExample();
        omsOrderExample.createCriteria().andReceiverPhoneEqualTo(conditions);
        omsOrderExample.or(omsOrderExample.createCriteria().andReceiverPhoneEqualTo(conditions));
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        omsOrderExample.createCriteria().andStatusBetween(1, 3);
        //confirm_status 确认收货状态：0->未确认；1->已确认
        omsOrderExample.createCriteria().andConfirmStatusEqualTo(0);
        //delete_status 删除状态：0->未删除；1->已删除
        omsOrderExample.createCriteria().andDeleteStatusEqualTo(0);
        omsOrderExample.setOrderByClause("create_time desc");
        //查询订单
        List<OmsOrder> omsOrders = orderMapper.selectByExample(omsOrderExample);
        if (omsOrders != null && omsOrders.size() > 0) {
            List<Long> orderIds = new ArrayList<>();
            for (OmsOrder order : omsOrders) {
                orderIds.add(order.getId());
            }
            Map<Long, List<OmsOrderItem>> itemMap = new HashMap<>();
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdIn(orderIds);
            List<OmsOrderItem> omsOrderItems = orderItemMapper.selectByExample(orderItemExample);
            for (OmsOrderItem item : omsOrderItems) {
                if (itemMap.get(item.getOrderId()) == null) {
                    itemMap.put(item.getOrderId(), new ArrayList<OmsOrderItem>());
                }
                itemMap.get(item.getOrderId()).add(item);
            }

            for (OmsOrder order : omsOrders) {
                OnePageOrderDetail onePageOrderDetail = new OnePageOrderDetail();
                onePageOrderDetail.setId(order.getId());  //订单id
                onePageOrderDetail.setMemberId(order.getMemberId());
                onePageOrderDetail.setOrderSn(order.getOrderSn());  //订单编号
                onePageOrderDetail.setPayType(order.getPayType());  //支付方式：0->未支付；1->支付宝；2->微信
                onePageOrderDetail.setPayAmount(order.getPayAmount());  //应付金额（实际支付金额）
                onePageOrderDetail.setReceiverName(order.getReceiverName());  //收货人姓名
                onePageOrderDetail.setReceiverPhone(order.getReceiverPhone());  //收货人电话
                onePageOrderDetail.setReceiverProvince(order.getReceiverProvince());  //省份/直辖市
                onePageOrderDetail.setReceiverCity(order.getReceiverCity());  //城市
                onePageOrderDetail.setReceiverRegion(order.getReceiverRegion());  //区
                onePageOrderDetail.setReceiverDetailAddress(order.getReceiverDetailAddress());  //详细地址
                onePageOrderDetail.setStatus(order.getStatus());  //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
                onePageOrderDetail.setDeliveryCompany(order.getDeliveryCompany());  //物流公司(配送方式)
                onePageOrderDetail.setDeliverySn(order.getDeliverySn());  //物流单号
                List<OmsOrderItem> itemList = itemMap.get(order.getId());
                List<OnePageOrderItem> orderItems = new ArrayList<>();
                onePageOrderDetail.setOrderItemList(orderItems);
                if (itemList != null) {
                    for (OmsOrderItem item : itemList) {
                        OnePageOrderItem orderItem = new OnePageOrderItem();
                        orderItem.setProductName(item.getProductName());
                        orderItem.setProductId(item.getProductId());
                        orderItem.setProductPic(item.getProductPic());
                        orderItem.setProductBrand(item.getProductBrand());
                        orderItem.setProductAttr(item.getProductAttr());
                        orderItem.setProductPrice(item.getProductPrice());
                        orderItem.setProductQuantity(item.getProductQuantity());
                        orderItems.add(orderItem);

//                        OmsOrderReturnApplyExample applyExample = new OmsOrderReturnApplyExample();
//                        applyExample.createCriteria().andOrderIdEqualTo(order.getId());
//                        applyExample.createCriteria().andProductIdEqualTo(item.getId());
//                        orderItem.setOrderReturnApplies(returnApplyMapper.selectByExample(applyExample));
                    }
                }
                orderDetailList.add(onePageOrderDetail);
            }
        }
        return orderDetailList;
    }

    @Override
    public CommonResult doWeChatPayByThirdParty(String orderNo, Long orderId, String frontUrl, String type, String body) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        BigDecimal payAmount = order.getPayAmount();

        String merchantNo = PAY_MERCHANTNO;
        String amount = payAmount + "";
        String notifyUrl = PAY_NOTIFYURL;
        Map<Integer, String> integerStringMap = this.wxPay(merchantNo, amount, orderNo, body, type, notifyUrl, frontUrl);

        return CommonResult.success(integerStringMap);
    }

    @Transactional
    @Override
    public CommonResult paySuccess(String orderNo) {
        OmsOrderExample omsOrderExample = new OmsOrderExample();
        omsOrderExample.createCriteria().andOrderSnEqualTo(orderNo);
        List<OmsOrder> omsOrders = orderMapper.selectByExample(omsOrderExample);
        if (omsOrders != null) {
            OmsOrder orderInfo = omsOrders.get(0);
            Long orderId = orderInfo.getId();
            //修改订单支付状态
            OmsOrder order = new OmsOrder();
            order.setId(orderId);
            order.setStatus(1);
            order.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            //恢复所有下单商品的锁定库存，扣减真实库存
            OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
            int count = portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
            return CommonResult.success(count, "支付成功");
        }
        return CommonResult.failed("支付回调异常");
    }

    @Transactional
    @Override
    public CommonResult submitOrder(OrderSubmitParam orderSubmitParam) {

        UmsMemberExample umsMemberExample = new UmsMemberExample();
        umsMemberExample.createCriteria().andPhoneEqualTo(orderSubmitParam.getPhone());
        umsMemberExample.or(umsMemberExample.createCriteria().andUsernameEqualTo(orderSubmitParam.getUsername()));
        umsMemberExample.setOrderByClause("create_time desc");
        List<UmsMember> umsMembers = memberMapper.selectByExample(umsMemberExample);
        UmsMember member = null;
        if (umsMembers != null && umsMembers.size() > 0) {
            member = umsMembers.get(0);

            UmsMember umsMember = new UmsMember();
            umsMember.setUsername(orderSubmitParam.getUsername());
            umsMember.setNickname(orderSubmitParam.getUsername());
            umsMember.setPhone(orderSubmitParam.getPhone());
            umsMember.setId(member.getId());
            memberMapper.updateByPrimaryKeySelective(umsMember);
        } else {
            //添加用户记录
            UmsMember umsMember = new UmsMember();
            umsMember.setUsername(orderSubmitParam.getUsername());
            umsMember.setNickname(orderSubmitParam.getUsername());
            umsMember.setPhone(orderSubmitParam.getPhone());
            umsMember.setPassword(passwordEncoder.encode("888888"));
            umsMember.setCreateTime(new Date());
            umsMember.setStatus(1);
            umsMember.setCity(orderSubmitParam.getCity());
            //获取默认会员等级并设置
            UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
            levelExample.createCriteria().andDefaultStatusEqualTo(1);
            List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
            if (!CollectionUtils.isEmpty(memberLevelList)) {
                umsMember.setMemberLevelId(memberLevelList.get(0).getId());
            }
            memberMapper.insert(umsMember);

            UmsMemberExample umsMemberExample2 = new UmsMemberExample();
            umsMemberExample2.createCriteria().andPhoneEqualTo(orderSubmitParam.getPhone());
            umsMemberExample2.setOrderByClause("create_time desc");
            List<UmsMember> umsMembers2 = memberMapper.selectByExample(umsMemberExample2);
            member = umsMembers2.get(0);
        }

        UmsMemberReceiveAddressExample addressExample = new UmsMemberReceiveAddressExample();
        addressExample.createCriteria().andMemberIdEqualTo(member.getId());

        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = addressMapper.selectByExample(addressExample);
        UmsMemberReceiveAddress receiveAddressInfo = null;
        if (umsMemberReceiveAddresses != null && umsMemberReceiveAddresses.size() > 0) {
            receiveAddressInfo = umsMemberReceiveAddresses.get(0);
            //更新地址记录
            UmsMemberReceiveAddress receiveAddress = new UmsMemberReceiveAddress();
            receiveAddress.setId(receiveAddressInfo.getId());
            receiveAddress.setMemberId(member.getId());
            receiveAddress.setName(member.getUsername());
            receiveAddress.setPhoneNumber(member.getPhone());
            receiveAddress.setDefaultStatus(1);
            receiveAddress.setProvince(orderSubmitParam.getProvince());
            receiveAddress.setCity(orderSubmitParam.getCity());
            receiveAddress.setRegion(orderSubmitParam.getRegion());
            receiveAddress.setDetailAddress(orderSubmitParam.getDetailAddress());
            addressMapper.updateByPrimaryKeySelective(receiveAddress);
        } else {
            //生成地址记录
            UmsMemberReceiveAddress receiveAddress = new UmsMemberReceiveAddress();
            receiveAddress.setMemberId(member.getId());
            receiveAddress.setName(member.getUsername());
            receiveAddress.setPhoneNumber(member.getPhone());
            receiveAddress.setDefaultStatus(1);
            receiveAddress.setProvince(orderSubmitParam.getProvince());
            receiveAddress.setCity(orderSubmitParam.getCity());
            receiveAddress.setRegion(orderSubmitParam.getRegion());
            receiveAddress.setDetailAddress(orderSubmitParam.getDetailAddress());
            addressMapper.insertSelective(receiveAddress);


            List<UmsMemberReceiveAddress> umsMemberReceiveAddresses2 = addressMapper.selectByExample(addressExample);
            receiveAddressInfo = umsMemberReceiveAddresses2.get(0);
        }


        List<OrderItemParam> orderItemParamList = orderSubmitParam.getOrderItemList();
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        List<Long> skuIds = new ArrayList<>();
        for (OrderItemParam orderItemParam : orderItemParamList) {
            PmsProduct pmsProduct = productMapper.selectByPrimaryKey(orderItemParam.getProductId());
            //生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(orderItemParam.getProductId());
            orderItem.setProductName(pmsProduct.getName());
            orderItem.setProductPic(pmsProduct.getPic());
            orderItem.setProductAttr(orderItemParam.getProductAttr());
            orderItem.setProductBrand(pmsProduct.getBrandName());
            orderItem.setProductSn(pmsProduct.getProductSn());
            orderItem.setProductPrice(pmsProduct.getPrice());
            orderItem.setProductQuantity(orderItemParam.getProductQuantity());
            orderItem.setProductSkuId(orderItemParam.getSkuId());

            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(orderItemParam.getSkuId());
            orderItem.setProductSkuCode(skuStock.getSkuCode());
            orderItem.setProductCategoryId(pmsProduct.getProductCategoryId());
            orderItem.setPromotionAmount(new BigDecimal(0));
            orderItem.setPromotionName(null);
            orderItem.setGiftIntegration(0);
            orderItem.setGiftGrowth(0);

            skuIds.add(skuStock.getId());
            orderItemList.add(orderItem);
        }

        //判断购物车中商品是否都有库存
        if (!hasStock(skuIds)) {
            return CommonResult.failed("库存不足，无法下单");
        }
        //判断使用使用了优惠券
        if (orderSubmitParam.getCouponId() == null) {
            //不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            //使用优惠券(暂时无优惠券)
//            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
//            if (couponHistoryDetail == null) {
//                return CommonResult.failed("该优惠券不可用");
//            }
//            //对下单商品的优惠券进行处理
//            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分
        if (orderSubmitParam.getUseIntegration() == null) {
            //不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            //使用积分(暂时无积分)
//            BigDecimal totalAmount = calcTotalAmount(orderItemList);
//            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
//            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
//                return CommonResult.failed("积分不可用");
//            } else {
//                //可用情况下分摊到可用商品中
//                for (OmsOrderItem orderItem : orderItemList) {
//                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
//                    orderItem.setIntegrationAmount(perAmount);
//                }
//            }
        }
        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        //进行库存锁定
        lockStock(orderItemList);
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderSubmitParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderSubmitParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderSubmitParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderSubmitParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setMemberId(member.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(member.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderSubmitParam.getPayType());
        //订单来源：0->PC订单；1->app订单 2->微信H5订单
        order.setSourceType(2);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = this.getItem(receiveAddressInfo.getId(), member.getId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        //计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        // TODO: 2018/9/3 bill_*,delivery_*
        //业务员id
        order.setEmployeeId(orderSubmitParam.getEmployeeId());
        //插入order表和order_item表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderSubmitParam.getCouponId() != null) {
            updateCouponStatus(orderSubmitParam.getCouponId(), member.getId(), 1);
        }
        //如使用积分需要扣除积分
        if (orderSubmitParam.getUseIntegration() != null) {
            order.setUseIntegration(orderSubmitParam.getUseIntegration());
            memberService.updateIntegration(member.getId(), member.getIntegration() - orderSubmitParam.getUseIntegration());
        }
        //删除购物车中的下单商品(暂无购物车)
//        deleteCartItemList(cartPromotionItemList, currentMember);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return CommonResult.success(result, "下单成功");
    }

    @Override
    public String getRedirectUrl(Long employeeId, Long productId) {
        DmsDomainSetting setting = domainSettingMapper.selectByPrimaryKey(1l);
        if (setting == null) {
            return null;
        }
        //拼接落地域名
        String text = "http://" + setting.getLandingDomain() + "/#/goods"
                + "?employeeId=" + employeeId
                + "&productId=" + productId;

        return text;
    }

    @Override
    public CommonResult isPayWxPay(String subject, Long orderId, String attachData, String orderNumber, String return_url) {
        if (subject.equals("") || orderNumber.equals("") || return_url.equals("")){
            System.out.println("输入不能有空！");
            return CommonResult.failed("输入不能有空！");
        }

        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);

        BigDecimal multiply = order.getPayAmount().multiply(new BigDecimal(100));

//        String param = "payId=" + PAY_ID
//                +"&payChannel="+ PAY_CHANNEL
//                +"&Subject="+ subject
//                +"&Money="+ multiply.intValue()
//                +"&orderNumber="+ orderNumber
//                +"&attachData="+ attachData
//                +"&Notify_url="+ NOTIFY_URL
//                +"&Return_url="+ return_url;

        SortedMap<Object, Object> sorts = new TreeMap<>();
        sorts.put("payId",PAY_ID);
        sorts.put("payChannel",PAY_CHANNEL);
        sorts.put("Subject",subject);
        sorts.put("Money", multiply.intValue());
        sorts.put("orderNumber",orderNumber);
        sorts.put("attachData",attachData);
        String sign = IsPayUtil.generate_sign(sorts, PAY_KEY);
        sorts.put("Sign",sign);
        sorts.put("Notify_url",NOTIFY_URL);
        sorts.put("Return_url",return_url);
        sorts.put("payKey",PAY_KEY);
        Map<Object,Object> paramters = new HashMap<>();
        for(Map.Entry<Object,Object> entry:sorts.entrySet()){
            paramters.put(entry.getKey(),entry.getValue());
        }
        JSONObject jsonObject = JSONObject.fromObject(sorts);

        //统一下单
        String result = HttpClientUtil.doPost("https://pay.ispay.cn/core/api/request/pay/",jsonObject);
        //String result = HttpClientUtil.post("https://pay.ispay.cn/core/api/request/pay/", null, paramters);

        if(StringUtils.isEmpty(result)){
            return  CommonResult.failed("ISPAY统一下单失败！");
        }

        return CommonResult.success(result);
    }

    @Override
    public String verifyCallBackSign(IsPayCallBackParam callBackParam) {
        //回调验签
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("Money",callBackParam.getMoney());
        parameters.put("attachData",callBackParam.getAttachData());
        parameters.put("orderNumber",callBackParam.getOrderNumber());
        parameters.put("payChannel", callBackParam.getPayChannel());
        String sign = IsPayUtil.generate_sign(parameters, PAY_KEY);
        if(sign.equals(callBackParam.getCallbackSign())){
            return "SUCCESS";
        }
        return null;
    }


    public UmsMemberReceiveAddress getItem(Long id, Long memberId) {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(memberId).andIdEqualTo(id);
        List<UmsMemberReceiveAddress> addressList = addressMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(addressList)) {
            return addressList.get(0);
        }
        return null;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) return;
        //查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_KEY_PREFIX_ORDER_ID + date;
        Long increment = redisService.increment(key, 1);
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }


    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(",");
        }
        String result = sb.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 锁定下单商品的所有库存
     */
    private void lockStock(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem item : orderItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + item.getProductQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销价格-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }


    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<Long> skuIds) {
        for (Long skuId : skuIds) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            if (skuStock == null) {
                return false;
            } else {
                Integer realStock = skuStock.getStock() - skuStock.getLockStock();
                if (realStock <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 第三方微信支付
     * Create by zhuyong on 2019/7/16
     */
    public Map<Integer, String> wxPay(String merchantNo, String amount, String orderNo, String body, String type, String notifyUrl, String frontUrl) {

        Map<Integer, String> result = new HashMap<>();
        if (amount.equals("") || frontUrl.equals("") || merchantNo.equals("")
                || notifyUrl.equals("") || orderNo.equals("") || type.equals("")) {
            System.out.println("输入不能有空！");

            result.put(1, "输入不能有空！");
            return result;
        }

        String param = "amount=" + amount
                + "&frontUrl=" + frontUrl
                + "&merchantNo=" + merchantNo
                + "&notifyUrl=" + notifyUrl
                + "&orderNo=" + orderNo
                + "&type=" + type
                + "&body=" + body;

        String sign = UtilTools.generate_sign(param, PAY_SIGNKEY);
        String sr = HttpTool.sendPost(UtilTools.getWxPayUrl(), param + "&sign=" + sign);
        System.out.println(sr);
        result.put(2, sr);
        return result;
    }

}
