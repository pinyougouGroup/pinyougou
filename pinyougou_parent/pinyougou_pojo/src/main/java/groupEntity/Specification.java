package groupEntity;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

public class Specification implements Serializable {

	private TbSpecification  tbSpecification;
	
	private List<TbSpecificationOption>  tbSpecificationOptionList;

	public TbSpecification getTbSpecification() {
		return tbSpecification;
	}

	public void setTbSpecification(TbSpecification tbSpecification) {
		this.tbSpecification = tbSpecification;
	}

	public List<TbSpecificationOption> getTbSpecificationOptionList() {
		return tbSpecificationOptionList;
	}

	public void setTbSpecificationOptionList(List<TbSpecificationOption> tbSpecificationOptionList) {
		this.tbSpecificationOptionList = tbSpecificationOptionList;
	}
	
}
