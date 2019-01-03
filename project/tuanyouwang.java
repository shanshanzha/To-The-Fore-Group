package com.weixin.myactivity.service;


public class TotalServices {

	/**
	 * 
	    * @Title: total  
        * @Description: 新建团队，保存团队信息，个人信息，更新用户信息，所在城市信息，省  
	    * @param @param wxName
	    * @param @param grade
	    * @param @param classint
	    * @param @param province
	    * @param @param city
	    * @param @param county
	    * @param @param name1
	    * @param @return
	    * @return String
	    * @throws
	 */
	public String total(String wxName, int grade, int classint, String province, String city, String county,
			String name1) {
		
		int s = this.schoolsServicesImpl.findOne(province, city, county, name1);
		System.out.println(s);
		if (s == 0) {
			this.schoolsServicesImpl.save(province, city, county, name1);
			s = this.schoolsServicesImpl.findOne(province, city, county, name1);
		}
		this.myClassServiceImpl.save(grade, classint, province, city, county, name1);
		int classId = this.myClassServiceImpl.findOne(grade, classint, s);
		Classes c = this.myClassServiceImpl.findClass(classId);
		c.getSchool().setSchoolId(s);
		User u = this.userServicesImpl.findOne(wxName);
		u.getSchool().setSchoolId(s);
		u.getClasss().setClassId(c.getClassId());
		try {
			this.userDaoImpl.update(u);
		} catch (Exception e) {
			e.printStackTrace();
		}
		User user = new User();
		Classes classes = new Classes();
		Schools school = new Schools();
		user.setClasss(classes);
		user.setSchool(school);
		user.getSchool().setSchoolId(u.getSchool().getSchoolId());
		user.getClasss().setClassId(u.getClasss().getClassId());
		user.setName(u.getName());
		user.setWxName(u.getWxName());
		user.setStatus(u.getStatus());
		user.setStudentId(u.getStudentId());
		JSONObject obj = JSONObject.fromObject(user);
		return obj.toString();

	}
	/**
	 * 
	    * @Title: findOne  
	    * @Description: 根据学校信息ID来查询团队信息  
	    * @Param@param grade
	    * @Param@param classInt
	    * @Param@return
	    * @Return Classes
	    * @throws
	 */
	public int findOne(int grade, int classInt, int schoolId) {

		String hql = "from Classes cl where cl.grade=? and cl.classInt=? ";
		Object[] ob = new Object[2];
		ob[0] = grade;
		ob[1] = classInt;
		Classes c1 = new Classes();
		try {
			List<Classes> c2 = this.myClassDaoImpl.find(hql, ob);
			for (Classes c : c2) {
				if (c.getSchool().getSchoolId() == schoolId) {
					c1.setClassId(c.getClassId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return c1.getClassId();
	}
	/**
	 * 
	    * @Title: updateCourse  
	    * @Description:根据团队ID修改团队信息
	    * @param @param classId
	    * @param @param lists
	    * @return void
	    * @throws
	 */
	public void updateCourse(String classId, String lists) {

		String[] list = lists.split(",");
		List list0 = new ArrayList();
		int classId1;
		if (classId == null) {
			classId = "1";
		}
		classId1 = Integer.parseInt(classId);

		for (int i = 0; i < list.length; i++) {
			list0.add(list[i]);
		}
		for (int i = 0; i < list0.size(); i++) {

			Course course = new Course();
			Classes classes = new Classes();
			classes.setClassId(classId1);
			int workday = (i % 5) + 1;
			int lesson = (i / 5) + 1;

			course.setWorkday(workday);
			course.setLesson(lesson);
			course.setContent((String) list0.get(i));
			course.setClasses(classes);
			Object[] object = new Object[2];
			object[0] = classId1;

			try {
				courseDaoImpl.save(course);
				object[1] = course.getId();
				this.courseDaoImpl.excuteBySql("update tbl_course set classId=?  where id=?", object);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	/**
	 * 
	    * @Title: saveStudents  
	    * @Description: 根据团队ID查询出团队中每个人的信息
	    * @param @param studentNames
	    * @param @param classId
	    * @param @return
	    * @return String
	    * @throws
	 */
	public String saveStudents(String stuNames, int classId) {
		String[] strs = stuNames.split(" ");
		for(int i = 1; i < strs.length; i++) {
			System.out.println("student name" + strs[i]);
			Students s = new Students();
			Classes c = new Classes();
			s.setName(strs[i]);
			s.setClasss(c);
			s.getClasss().setClassId(classId);
			try {
				this.addStudentDaoImpl.save(s);
			} catch (Exception e) {
				e.printStackTrace();
				return "false";
			}
		}
		return "true";
		
	}
	/**
	 * 
	    * @Title: findOneTeacher  
	    * @Description: 查询数据库得到出去玩的团队信息
	    * @param @param userName
	    * @param @param status
	    * @param @return
	    * @return String
	    * @throws
	 */
	public String findOneTeacher(String wxName, String status) {
		User user = new User();
		String hql = "from User where wxName=?";
		Object[] obj = new Object[1];
		obj[0] = wxName;
		try {
			User u = this.userDaoImpl.findOne(hql, obj);
			if (u == null) {
				user.setName("无名氏");
				user.setWxName(wxName);
				user.setStatus(Integer.parseInt(status));
				this.userDaoImpl.save(user);
				JSONObject jb1 = JSONObject.fromObject(user);
				return jb1.toString();
			} else {
				user.setName(u.getName());
				user.setId(u.getId());
				user.setWxName(u.getWxName());
				user.setStatus(u.getStatus());
				JSONObject jb1 = JSONObject.fromObject(user);
				return jb1.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
