/**
 * 
 */
package org.zlong.repository;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;
import org.zlong.model.User;

/**
 * @author zhanglong
 * 
 */
@Repository
public class UserRepository {

	private static final ConcurrentHashMap<Integer, User> USER_MAP = new ConcurrentHashMap<>();

	private static AtomicInteger atomicUserId;

	public int add(User user) {
		int userId = atomicUserId.incrementAndGet();
		user.setId(userId);
		USER_MAP.put(userId, user);
		return userId;
	}

	public User get(int userId) {
		return USER_MAP.get(userId);
	}

	public List<User> getAll() {
		Enumeration<User> elements = USER_MAP.elements();

		List<User> users = new ArrayList<>();
		while (elements.hasMoreElements()) {
			users.add(elements.nextElement());
		}
		return users;
	}
}
