package entity;

import java.io.Serializable;

public class UserIdAndSeckillGoodsId implements Serializable{

	private String userId;
	private Long id;
	public UserIdAndSeckillGoodsId(String userId, Long id) {
		super();
		this.userId = userId;
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
