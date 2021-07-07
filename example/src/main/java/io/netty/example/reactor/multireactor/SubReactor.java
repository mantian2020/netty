package io.netty.example.reactor.multireactor;

import io.netty.example.reactor.SelfRunable;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author hqg
 * @ClassName: SubReactor
 * @Description: TODO
 * @date 2021/7/7 15:14
 */
public class SubReactor implements SelfRunable {

    private Selector selector;
    private String name;
    private List<SelfRunable> task = new ArrayList<SelfRunable>();

    public SubReactor(String name) throws IOException {
        this.name = name;
        selector = Selector.open();
        new Thread(this).start();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("subReactor start select event...");
                selector.select(5000);
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey)it.next());
                }
                selectedKeys.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        SelfRunable r = (SelfRunable) key.attachment();
        if (r != null) {
            System.out.println("subReactor dispatch to " + r.getName() + "====");
            r.run();
        }
    }

    public Selector getSelector(){
        return this.selector;
    }

    public void submit(SelfRunable runnable) {
        task.add(runnable);
    }

}
