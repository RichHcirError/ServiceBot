package com.neznayka.www.service;


import com.neznayka.www.dao.config.ConfigDictionaryDAOIntf;
import com.neznayka.www.hibernate.Logging;
import com.neznayka.www.hibernate.Message;
import com.neznayka.www.model.CRUDRequestResponse;
import com.neznayka.www.model.MessageAnswer;
import com.neznayka.www.model.Pager;
import com.neznayka.www.utils.BotUtilMethods;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;



@Service
public class LoggingService  {

        private static final String CLASS_NAME = LoggingService.class.getName();
        private static final Logger log = Logger.getLogger(CLASS_NAME);
        @Autowired
        @Qualifier("sessionFactory")
        private SessionFactory sessionFactory;


        private Long getId(){
                Session session = sessionFactory.getCurrentSession();
                Query query = session.createSQLQuery( "select nextval('log_id_seq')" );
                Long id = ((BigInteger) query.uniqueResult()).longValue();
                return id;
        }

        @Transactional
        public void logMessage(Long id, String question, String message){
                Session session = sessionFactory.getCurrentSession();

                if(id==null){
                        id=getId();
                }
                String text = BotUtilMethods.getPropertyFromJSON(question, "text");
                log.info("ID="+id+" QUESTIONS="+text+" MESSAGE="+message);
                Logging logging = new Logging();
                logging.setId(id);
                logging.setQuestion(text);
                logging.setMessage(message);
                session.save(logging);

        }


}





