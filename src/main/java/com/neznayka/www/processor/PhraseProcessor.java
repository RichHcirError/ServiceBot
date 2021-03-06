package com.neznayka.www.processor;

import com.neznayka.www.dao.config.ConfigDictionaryDAOIntf;
import com.neznayka.www.hibernate.Message;
import com.neznayka.www.hibernate.Tag;


import com.neznayka.www.model.MessageAnswer;
import com.neznayka.www.utils.BotUtilMethods;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Polulyakh Denis
 * @date 19.03.2017
 */
public class PhraseProcessor {
    private static final String CLASS_NAME = "PhraseProcessor";
    private static final Logger log = Logger.getLogger(CLASS_NAME);
    private static final String DEFAULT_ANSWER="Уточните ваш вопрос";
    private ConfigDictionaryDAOIntf configDAO;

    public MessageAnswer getMessageToAnswer(String message) {
        MessageAnswer messageAnswer = new MessageAnswer();
        final String METHOD_NAME = "getMessageToAnswer";
        String text = BotUtilMethods.getPropertyFromJSON(message, "text");
        log.info(CLASS_NAME + " " + METHOD_NAME + " question: " + text);
        text = BotUtilMethods.replaseSymbols(text);
        text = text.toLowerCase();


        List<Message> listMessage = configDAO.searchAnswer(text.split(" "));
        log.info(listMessage.toString());

        if (listMessage != null) {
            if (listMessage.size() > 0 && listMessage.size() < 2) {
                messageAnswer.setPhrase(listMessage.get(0).getValue());
            }else {
                messageAnswer.setPhrase(getOnlyOneMessage(listMessage, text.split(" ")).getValue());
            }
        }

        if(messageAnswer.getPhrase().equalsIgnoreCase(DEFAULT_ANSWER)){
            messageAnswer.setFound(false);
        }else{
            messageAnswer.setFound(true);
        }

        return  messageAnswer;
    }


    private Message getOnlyOneMessage(List<Message> messages, String[] keys) {
        log.info(messages.toString());
        log.info("SIZE "+messages.size());
        Map<Integer, Message> orderMatchesMap = new TreeMap<Integer, Message>();
        // ищем сколько раз встречается один итот же message id
        for (Message m : messages) {
            int match = 0;
            int id=m.getId();
            for (Message m1 : messages) {
                if(m1.getId()==id){
                    match++;
                }
            }
            if (match > 1) {
                orderMatchesMap.put(match, m);
            }
        }

        Message findMessage=new Message();
        findMessage.setValue(DEFAULT_ANSWER);
        //ищем максимально вхождение с одинаковым message id
        if(orderMatchesMap.size()>0) {
            Object[] keysMap = orderMatchesMap.keySet().toArray();
            int highestKey = (Integer) keysMap[keysMap.length - 1];
            findMessage = orderMatchesMap.get(highestKey);
            log.info("MAX MATCHES ID: "+highestKey);
        }
        log.info("OrderMatches: "+orderMatchesMap);


        log.info(findMessage.toString());

        return findMessage;

    }


    public void setConfigDAO(ConfigDictionaryDAOIntf configDAO) {
        this.configDAO = configDAO;
    }
}
