package com.netty.configcenter.test;

import com.netty.configcenter.event.ConfigEvent;
import com.netty.configcenter.event.MessageChangedEvent;
import com.netty.configcenter.listener.MessageConfigListener;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author：zeqi
 * @Date: Created in 17:31 7/2/18.
 * @Description:
 */
public class GeneircTest {




    @Test
    public void testGeneircTest() {
        MessageConfigListener<MessageChangedEvent> listener = new MessageConfigListener<MessageChangedEvent>() {
            @Override
            public void messageChanged(MessageChangedEvent event) {

            }
        };

        ConfigEvent event = new MessageChangedEvent("","","");

        System.out.println("MessageChangedEvent equals " + event.getClass());

        Class<?> clazz = listener.getClass();

        Type[] types = clazz.getGenericInterfaces();

        System.out.println(types[0].getTypeName());

        if (types[0] instanceof  ParameterizedType) {
            Type[] subTypes = ((ParameterizedType) types[0]).getActualTypeArguments();

            System.out.println(((Class)subTypes[0]).getName());
            System.out.println(((Class)subTypes[0]).isAssignableFrom(event.getClass()));

        }


    }

}
