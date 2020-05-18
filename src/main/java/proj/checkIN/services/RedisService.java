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
	
	public void test_del() {
		
		//Connecting to Redis server on localhost 
		Jedis jedis = new Jedis();

		String agentID = "abcd@naver.com";
	    System.out.println("Connection to server sucessfully"); 
	    //set the data in redis string 
	    jedis.del(agentID);
	    
	    // Get the stored data and print it 
	    jedis.close();
	}
	
	public boolean setData(String agentID, String number) {
		Jedis jedis = new Jedis();
		jedis.set(agentID, number);
		jedis.expire(agentID, 61);
		jedis.close();
		return true;
	}
	
	public String getData(String agentID) {
		Jedis jedis = new Jedis();
		String value = jedis.get(agentID);
		jedis.del(agentID);
		jedis.close();
		return value;
	}
}