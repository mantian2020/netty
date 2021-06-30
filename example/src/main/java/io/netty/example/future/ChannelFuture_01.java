package io.netty.example.future;

import io.netty.util.concurrent.*;

import java.util.concurrent.ThreadFactory;

/**
 * @author hqg
 * @ClassName: ChannelFuture_01
 * @Description: TODO
 * @date 2021/6/29 15:39
 */
public class ChannelFuture_01 {

    public static void main(String[] args) throws InterruptedException {

        EventExecutor executor = new SelfEventExecutor();
        final Promise<String> promise = new DefaultPromise<String>(executor);


        promise.addListener(new GenericFutureListener<Future<? super String>>() {
            @Override
            public void operationComplete(Future<? super String> future) throws Exception {
                System.out.println("执行完成结果：" + future.get());
            }
        });


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    promise.setSuccess("hello world!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();

        promise.sync();
        System.out.println("==================");
    }

    static class SelfEventExecutor extends SingleThreadEventExecutor {


        public SelfEventExecutor() {
            this(null);
        }

        public SelfEventExecutor(EventExecutorGroup parent) {
            this(parent, new DefaultThreadFactory(SelfEventExecutor.class));
        }

        public SelfEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory) {
            super(parent, threadFactory, true);
        }

        @Override
        protected void run() {
            Runnable task = takeTask();
            if (task != null) {
                task.run();
            }
        }
    }
}
