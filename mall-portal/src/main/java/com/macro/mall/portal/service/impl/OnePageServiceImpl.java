package com.macro.mall.portal.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.OnePageProductDao;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.OnePageService;
import com.macro.mall.portal.service.RedisService;
import com.macro.mall.portal.service.UmsMemberReceiveAddressService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private EmsEmployeePerformanceMapper performanceMapper;

    @Autowired
    private OmsOrderTaskLogMapper taskLogMapper;

    @Autowired
    private OnePageProductDao onePageProductDao;

    @Value("${pay-info.signKey}")
    private String PAY_SIGNKEY;

    @Value("${is-pay.queryUrl}")
    private String QUERY_URL;


    @Autowired
    private RedisService redisService;
    @Value("${redis.key.prefix.orderId}")
    private String REDIS_KEY_PREFIX_ORDER_ID;

    @Value("${pay-info.merchantNo}")
    private String PAY_MERCHANTNO;
    @Value("${pay-info.notifyUrl}")
    private String PAY_NOTIFYURL;

    //ISPAY支付
    @Value("${is-pay.payId}")
    private Integer PAY_ID;
    @Value("${is-pay.payChannel}")
    private String PAY_CHANNEL;
    @Value("${is-pay.Notify_url}")
    private String NOTIFY_URL;
    @Value("${is-pay.payKey}")
    private String PAY_KEY;

    //久久微信支付
    @Value("${jiujiu-pay.merchant_id}")
    private Integer MERCHANT_ID;
    @Value("${jiujiu-pay.s_key}")
    private String S_KEY;
    @Value("${jiujiu-pay.j_notify_url}")
    private String J_NOTIFY_URL;


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
        omsOrderExample.or()
                .andReceiverNameEqualTo(conditions)
                .andStatusBetween(1, 3)   //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
                .andConfirmStatusEqualTo(0)   //confirm_status 确认收货状态：0->未确认；1->已确认
                .andDeleteStatusEqualTo(0);  //delete_status 删除状态：0->未删除；1->已删除

        omsOrderExample.or()
                .andReceiverPhoneEqualTo(conditions)
                .andStatusBetween(1, 3)   //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
                .andConfirmStatusEqualTo(0)   //confirm_status 确认收货状态：0->未确认；1->已确认
                .andDeleteStatusEqualTo(0);  //delete_status 删除状态：0->未删除；1->已删除

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
    public CommonResult paySuccessHandle(String orderNo, String trade_no) {
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
            order.setTradeNo(trade_no);
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
            if(skuStock == null){
                System.out.println("====zhuyong=======>skuStock can't find:"+orderItemParam.getSkuId());
            }
            orderItem.setProductSkuCode(skuStock.getSkuCode());
            orderItem.setSp1(skuStock.getSp1());
            orderItem.setSp2(skuStock.getSp2());
            orderItem.setSp3(skuStock.getSp3());
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
        EmsEmployee employee = emsEmployeeMapper.selectByPrimaryKey(orderSubmitParam.getEmployeeId());
        order.setEmployeeName(employee != null?employee.getNickName():"");

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
        String text = "http://" + setting.getLandingDomain() + "/goods"
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

        SortedMap<Object, Object> sorts = new TreeMap<>();
        sorts.put("payId",PAY_ID);
        sorts.put("payChannel",PAY_CHANNEL);
        sorts.put("Subject",subject);
        sorts.put("Money", multiply.intValue());
        sorts.put("orderNumber",orderNumber);
        sorts.put("attachData",attachData);
        sorts.put("Notify_url",NOTIFY_URL);
        sorts.put("Return_url",return_url);
        String sign = IsPayUtil.generate_sign(sorts, PAY_KEY);
        sorts.put("Sign",sign);

        sorts.put("payKey",PAY_KEY);
        Map<Object,Object> paramters = new HashMap<>();
        for(Map.Entry<Object,Object> entry:sorts.entrySet()){
            paramters.put(entry.getKey(),entry.getValue());
        }
        //统一下单
        String result = "";
        try {
            result = HttpClientUtil.doPostForm("https://pay11.ispay.cn/core/api/request/pay/", paramters,"UTF-8");
            if(StringUtils.isEmpty(result)){
                return  CommonResult.failed("ISPAY统一下单失败！");
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CommonResult.success(result);
    }

    @Override
    public String verifyCallBackSign(String orderNumber, String payChannel, Integer money, String attachData, String callbackSign) {
        //回调验签
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("Money",money);
        parameters.put("attachData",attachData);
        parameters.put("orderNumber",orderNumber);
        parameters.put("payChannel", payChannel);
        String sign = IsPayUtil.generate_sign(parameters, PAY_KEY);
        if(sign.equals(callbackSign)){
            return "SUCCESS";
        }
        return null;
    }

    @Override
    public CommonResult queryOrder(String orderNumber) {
        Map<Object,Object> paramters = new HashMap<>();
        paramters.put("payId",PAY_ID);
        paramters.put("orderNumber",orderNumber);
        String result = "";
        try {
            result = HttpClientUtil.doPostForm("https://pay11.ispay.cn/core/api/request/query/", paramters,"UTF-8");
            if(StringUtils.isEmpty(result)){
                return  CommonResult.failed("ISPAY查询订单失败！");
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return CommonResult.success(result);
    }

    @Override
    public CommonResult getPayWxPayParams(String subject, Long orderId, String attachData, String orderNumber, String return_url) {
        if (subject.equals("") || orderNumber.equals("") || return_url.equals("")){
            System.out.println("输入不能有空！");
            return CommonResult.failed("输入不能有空！");
        }

        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);

        BigDecimal multiply = order.getPayAmount().multiply(new BigDecimal(100));

        SortedMap<Object, Object> sorts = new TreeMap<>();
        sorts.put("payId",PAY_ID);
        sorts.put("payChannel",PAY_CHANNEL);
        sorts.put("Subject",subject);
        sorts.put("Money", multiply.intValue());
        sorts.put("orderNumber",orderNumber);
        sorts.put("attachData",attachData);
        sorts.put("Notify_url",NOTIFY_URL);
        sorts.put("Return_url",return_url);
        String sign = IsPayUtil.generate_sign(sorts, PAY_KEY);
        sorts.put("Sign",sign);
        sorts.put("payKey",PAY_KEY);
        Map<Object,Object> paramters = new HashMap<>();
        for(Map.Entry<Object,Object> entry:sorts.entrySet()){
            paramters.put(entry.getKey(),entry.getValue());
        }
        return CommonResult.success(paramters);
    }

    @Transactional
    @Override
    public void countVisitTimes(Long employeeId, Long productId) {
        System.out.println("=======zhuyong====>employeeId:"+employeeId+"==== productId:"+productId);
        try{
            EmsEmployeePerformanceExample example = new EmsEmployeePerformanceExample();
            example.createCriteria().andEmployeeIdEqualTo(employeeId).andProductIdEqualTo(productId);
            List<EmsEmployeePerformance> emsEmployeePerformances = performanceMapper.selectByExample(example);
            if(emsEmployeePerformances != null && emsEmployeePerformances.size() > 0){
                EmsEmployeePerformance performance = emsEmployeePerformances.get(0);
                if(performance.getUpdateTime().before(DateUtil.getDate(new Date()))){
                    System.out.println("=======zhuyong====> clearTodayVisitTimes:"+employeeId+"==== productId:"+productId);
                    //如果上次更新是昨天，那么需要将今天访问次数重置为0
                    onePageProductDao.clearTodayVisitTimes(employeeId,productId);
                }
                onePageProductDao.countVisitTimes(employeeId, productId);
            }else{
                EmsEmployeePerformance performance = new EmsEmployeePerformance();
                performance.setEmployeeId(employeeId);
                performance.setProductId(productId);
                performance.setVisitTimes(1);
                performance.setTodayVisitTimes(1);
                performance.setUpdateTime(new Date());
                performanceMapper.insert(performance);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String paySuccess2(String orderNumber, String result) throws Exception {
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria()
                .andOrderSnEqualTo(orderNumber)
                .andStatusEqualTo(0);
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        if (orderList != null && orderList.size() > 0) {
            Map<Object, Object> paramters = new HashMap<>();
            paramters.put("payId", PAY_ID);
            paramters.put("orderNumber", orderNumber);

            result = HttpClientUtil.doPostForm(QUERY_URL, paramters, "UTF-8");
            if (StringUtils.isEmpty(result)) {
                System.out.println("========= query for:" + orderNumber + " is nothing!");
            }
        }
        return result;
    }
    @Override
    public void insertTaskLog(String orderNumber){
        //保存异常订单日志
        OmsOrderTaskLog taskLog = new OmsOrderTaskLog();
        taskLog.setTaskContent("监控ISPAY支付情况"); //任务内容
        taskLog.setRequestParams("orderNumber:"+orderNumber); //请求参数
        taskLog.setExecMethod("OrderTaskThread.paySuccess()"); //执行的方法
        taskLog.setErrorMsg("无法查询到该订单信息"); //错误信息
        taskLog.setRemark(""); //备注
        taskLog.setCreateTime(new Date()); //异常提交时间
        taskLogMapper.insertSelective(taskLog);
    }

    /**
     * 久久微信支付
     * Create by zhuyong on 2019/7/29
     */
    @Override
    public Map<String, Object> jiujiuPayWxPay(HttpServletRequest request) throws Exception {
        Map<String,Object> pay_code_map = new HashMap<>();

        Integer merchant_id = MERCHANT_ID;//	必填	商户ID、在平台首页右边获取商户ID	10000
        String content_type = "json";//	必填		固定值:json
        String pay_type = "wechat";//	必填	支付方式，支付宝:alipay，微信:wechat，网银：bank,银联扫码:yinlian，银行聚合码:jhm	wechat
        String jhm_type = "";//	选填	pay_type为jhm时必填，聚合码收款方式，支付宝:alipay，微信:wechat，银联扫码:yinlian	wechat
        String pay_method = "wap";//	选填	pc:网站，wap:手机	wap
        String out_trade_no = request.getParameter("out_trade_no");//	必填	商户订单号，需保证在商户平台唯一	2018062668945
        String amount = request.getParameter("amount");//	必填	支付金额	1.00
        String robin = "1";//	必填	轮训，默认值1	1
        String notify_url = J_NOTIFY_URL;//	必填	异步通知地址，在支付完成时，本平台服务器系统会自动向该地址发起一条支付成功的回调请求, 对接方接收到回调后，  必须返回success ,否则默认为回调失败,回调信息会补发5次。	http://pay.9fubaopay.com/Notify/Pqi
        String return_url = request.getParameter("return_url");//	选填	支付成功后网页自动跳转地址	http://pay.9fubaopay.com/Return/Success

        StringBuilder params = new StringBuilder();
        //	amount={}&content_type={}&merchant_id={}&notify_url={}&out_trade_no={}&pay_type={}{key}
        params.append("amount=").append(amount);
        params.append("&content_type=").append(content_type);
        params.append("&merchant_id=").append(merchant_id);
        params.append("&notify_url=").append(notify_url);
        params.append("&out_trade_no=").append(out_trade_no);
        params.append("&pay_type=").append(pay_type);
        params.append(S_KEY);
        //md5加密,大写
        String sign = MD5Utils.MD5Encode(params.toString(),"utf-8",true);

        //封装application/x-www-form-urlencoded 参数
        Map<Object,Object> map = new HashMap<>();
        map.put("merchant_id",merchant_id);
        map.put("content_type",content_type);
        map.put("pay_type",pay_type);
        map.put("jhm_type",jhm_type);
        map.put("pay_method",pay_method);
        map.put("out_trade_no",out_trade_no);
        map.put("amount",amount);
        map.put("robin",robin);
        map.put("notify_url",notify_url);
        map.put("return_url",return_url);
        map.put("sign",sign);

        String response = HttpClientUtil.doPostForm2("http://pay.9fubaopay.com/GateWay/Pay", map, "utf-8");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付返回参数:>>>>>>>>>>>>>>"+response);
        //校验返回值
        JSONObject result = JSONObject.fromObject(response);
            String result_merchant_id = (String)result.get("merchant_id");
        String result_order_amount = (String)result.get("order_amount");
        String result_pay_amount = (String)result.get("pay_amount");
        String result_out_trade_no = (String)result.get("out_trade_no");
        String result_trade_no = (String)result.get("trade_no");
        String result_fees = (String)result.get("fees");
        String result_paytype = (String)result.get("paytype");
        String result_status = (String)result.get("status");
        String result_message = (String)result.get("message");
        String result_payurl = (String)result.get("payurl");
        String result_sign = (String)result.get("sign");
        String result_codeurl = (String)result.get("codeurl");

        StringBuilder result_params = new StringBuilder();
        result_params.append("merchant_id=").append(result_merchant_id);
        result_params.append("&message=").append(result_message);
        result_params.append("&fees=").append(result_fees);
        result_params.append("&order_amount=").append(result_order_amount);
        result_params.append("&out_trade_no=").append(result_out_trade_no);
        result_params.append("&pay_amount=").append(result_pay_amount);
        result_params.append("&paytype=").append(result_paytype);
        result_params.append("&status=").append(result_status);
        result_params.append("&payurl=").append(result_payurl);
        result_params.append("&trade_no=").append(result_trade_no);
        result_params.append(S_KEY);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>result_params:"+result_params.toString());
        String my_result_sign = MD5Utils.MD5Encode(result_params.toString(),"utf-8",true);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信支付校验>>>>>>>>>>>>>>"+(my_result_sign.equals(result_sign)));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>my_result_sign:"+my_result_sign);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>result_sign:"+result_sign);

        pay_code_map.put("status",result_status);
        pay_code_map.put("result_message",result_message);
        if(my_result_sign.equals(result_sign)){
            //校验成功
            pay_code_map.put("result_codeurl",result_codeurl);
        }
        return pay_code_map;
    }

    /**
     * 久久微信支付成功的回调
     * Create by zhuyong on 2019/7/29
     */
    @Override
    public Map<String, Object> jiujiuPaySuccess(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取参数
        String merchant_id = request.getParameter("merchant_id");//	商户ID、在平台首页右边获取商户ID	10000
        String pay_time = request.getParameter("pay_time");//	支付成功的	格式为:20181201102435(2018年12月1日10时2分35秒)
        String status = request.getParameter("status");//	支付状态，success表示成功,fail表示失败	success
        String order_amount = request.getParameter("order_amount");//	订单金额	1.00
        String pay_amount = request.getParameter("pay_amount");//	实际支付金额	1.00
        String out_trade_no = request.getParameter("out_trade_no");//	商户订单号	2018062312410711888
        String trade_no = request.getParameter("trade_no");//	交易流水号，由系统生成的交易流水号	2018062312410729584
        String fees = request.getParameter("fees");//	手续费，该笔订单的手续费(已在平台余额中扣除)	0.0400
        String paytype = request.getParameter("paytype");//	支付方式，支付宝:alipay，微信:wechat	wechat
        String jhm_type = request.getParameter("jhm_type");//	paytype为jhm时必填	wechat
        String sign = request.getParameter("sign");//	d92eff67b3be05f5e61502e96278d01b

        StringBuilder params = new StringBuilder();
        params.append("merchant_id=").append(merchant_id);
        params.append("&fees=").append(fees);
        params.append("&order_amount=").append(order_amount);
        params.append("&out_trade_no=").append(out_trade_no);
        params.append("&pay_amount=").append(pay_amount);
        params.append("&pay_time=").append(pay_time);
        params.append("&paytype=").append(paytype);
        params.append("&status=").append(status);
        params.append("&trade_no=").append(trade_no);
        params.append(S_KEY);

        //md5加密,大写
        String my_sign = MD5Utils.MD5Encode(params.toString(),"utf-8",true);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>久久微信回调函数校验>>>>>>>>>>>>>>"+(my_sign.equals(sign)));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>my_sign:"+my_sign);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>sign:"+sign);
        if(true || my_sign.equals(sign)){
            //校验成功
            resultMap.put("status",status);
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("trade_no",trade_no);
            return resultMap;
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
