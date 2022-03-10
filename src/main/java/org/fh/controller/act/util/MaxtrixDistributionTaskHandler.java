package org.fh.controller.act.util;

import org.apache.shiro.session.Session;
import org.fh.util.Jurisdiction;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 说明：矩阵分发下一任办理人
 *  作者：f-sci 
 *  授权：bsic
 */
@SuppressWarnings("serial")
@Component(value="deptListener")// 任务监听器的委托表达式为：${deptListener}
public class MaxtrixDistributionTaskHandler implements TaskListener {
	// 部门参数，在节点上新增一个任务监听器，添加一个Expression类型的成员，名称为dept_no
	// 如下的变量名要和流程图中添加的任务监听器的变量【名称】值相对应
	private Expression dept;
	@Override
	public void notify(DelegateTask delegateTask) {
		// 然后通过如下代码可以取到该成员的字符串
		String dept_value = (String)dept.getValue(delegateTask);
		// TODO 在下面实现矩阵分发的业务逻辑
		System.out.println(dept_value);
		// 将从矩阵中取到的办理人设置到当前任务
		delegateTask.setAssignee("ZHANG SAN");
		// 将分发结果设置到session
		Session session = Jurisdiction.getSession();
		session.setAttribute("TASKID", delegateTask.getId()); // 任务ID
		session.setAttribute("YAssignee", delegateTask.getAssignee()); // 待办人
	}

}
