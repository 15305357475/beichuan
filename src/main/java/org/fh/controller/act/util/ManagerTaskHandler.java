package org.fh.controller.act.util;

import org.apache.shiro.session.Session;
import org.fh.util.Jurisdiction;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 说明：指定下一任务待办人
 *  作者：f-sci 
 *  授权：bsic
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		Session session = Jurisdiction.getSession();
		session.setAttribute("TASKID", delegateTask.getId()); // 任务ID
		session.setAttribute("YAssignee", delegateTask.getAssignee()); // 默认待办人
	}

}
