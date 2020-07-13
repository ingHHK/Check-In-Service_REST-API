package proj.checkIN.services;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisService {
	public void test_set() {
		
		//Connecting to Redis server on localhost 
		Jedis jedis = new Jedis();
		String agentID = "abcd@naver.com";
		String num = "01231230";
	    System.out.println("Connection to server sucessfully"); 
	    //set the data in redis string 
	    jedis.set(agentID, num);
	    jedis.expire(agentID, 6);
	    // Get the stored data and print it 
	    jedis.close();
	}
	
	public void del(String agentID) {
		
		//Connecting to Redis server on localhost 
		Jedis jedis = new Jedis();
	    try{
	    	jedis.del(agentID);
	    }catch(Exception e){
	    	
	    }
	    jedis.close();
	}
	
	public boolean setCode(String verify_code, String agentID) {
		Jedis jedis = new Jedis();
		jedis.set(verify_code, agentID);
		jedis.expire(verify_code, 20);
		jedis.close();
		return true;
	}
	
	public String getCode(String verify_code) {
		Jedis jedis = new Jedis();
		String value = jedis.get(verify_code);
		jedis.del(verify_code);
		jedis.close();
		return value;
	}
	
	public boolean setToken(String agentID, String number) {
		Jedis jedis = new Jedis();
		jedis.set(agentID, number);
		jedis.expire(agentID, 3600);
		jedis.close();
		return true;
	}
	
	public String getToken(String agentID) {
		Jedis jedis = new Jedis();
		String value = jedis.get(agentID);
		jedis.close();
		return value;
	}
}