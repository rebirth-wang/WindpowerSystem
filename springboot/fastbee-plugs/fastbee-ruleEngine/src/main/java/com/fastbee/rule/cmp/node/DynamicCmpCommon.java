package com.fastbee.rule.cmp.node;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowRetry;
import com.yomahub.liteflow.core.NodeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LiteflowRetry(3)
@LiteflowComponent("DynamicCmpCommon")
public class DynamicCmpCommon extends NodeComponent {

	private static final Logger LOG = LoggerFactory.getLogger(DynamicCmpCommon.class);

	@Override
	public void process() {
		LOG.info(this.getClass().getSimpleName()+" executed!");
		System.out.println(this.getClass().getSimpleName()+" executed!");
	}

	// 表示是否进入该节点，可以用于业务参数的预先判断
	@Override
	public boolean isAccess() {
		return super.isAccess();
	}

	// 表示出错是否继续往下执行下一个组件，默认为false
	@Override
	public boolean isContinueOnError() {
		return super.isContinueOnError();
	}

	// 如果覆盖后，返回true，则表示在这个组件执行完之后立马终止整个流程。
	// 对于这种方式，由于是用户主动结束的流程，属于正常结束，所以最终的isSuccess是为true的。
	// 需要注意的是，如果isContinueOnError为true的情况下，调用了this.setIsEnd(true)，那么依旧会终止。response里的isSuccess还是true。
	@Override
	public boolean isEnd() {
		return super.isEnd();
	}

	// 前置处理器，在isAccess 之后执行
	@Override
	public void beforeProcess() {
		super.beforeProcess();
	}

	// 后置处理器
	@Override
	public void afterProcess() {
		super.afterProcess();
	}

	// 流程的成功事件回调
	@Override
	public void onSuccess() throws Exception {
		super.onSuccess();
	}

	// 流程的失败事件回调
	@Override
	public void onError(Exception e) throws Exception {
		super.onError(e);
	}

	// 是否回滚
	@Override
	public boolean isRollback() {
		return super.isRollback();
	}

	// 回滚方法
	@Override
	public void rollback() throws Exception {
		super.rollback();
	}
}
