package groupEntity;

import java.io.Serializable;
import java.util.Map;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

public class Orders implements Serializable {
	private TbOrder tbOrder;
	
	private TbOrderItem tbOrderItem;
	
	private  TbGoods tbGoods;
	

	

	public TbOrder getTbOrder() {
		return tbOrder;
	}

	public void setTbOrder(TbOrder tbOrder) {
		this.tbOrder = tbOrder;
	}

	public TbOrderItem getTbOrderItem() {
		
		return tbOrderItem;
	}

	public void setTbOrderItem(TbOrderItem tbOrderItem) {
		this.tbOrderItem = tbOrderItem;
	}

	public TbGoods getTbGoods() {
		return tbGoods;
	}

	public void setTbGoods(TbGoods tbGoods) {
		this.tbGoods = tbGoods;
	}
	
	

}
