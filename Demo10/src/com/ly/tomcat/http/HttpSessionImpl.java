package com.ly.tomcat.http;


import com.ly.standard.http.HttpSession;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 解析sessionId 是否在服务器中*/
public class HttpSessionImpl implements HttpSession {

    public final  String sessionId;
    public final Map<String, Object> sessionData;
    /** 构建新的session*/
    public HttpSessionImpl() {
        sessionId = UUID.randomUUID().toString();
        sessionData = new HashMap<>();
    }

    /** 找到新的session*/
    public HttpSessionImpl(String sessionId) throws IOException, ClassNotFoundException {
        this.sessionId = sessionId;
        this.sessionData = loadSessionData(sessionId);
    }

    //存储session的位置
    private static final String SESSION_SAVE = "C:\\JavaEE_study\\Demo10\\src\\saveSession";
    //每一个session都创建一个 sessionId.session的名字 里面保存sessionData
    private Map<String, Object> loadSessionData(String sessionId) throws IOException, ClassNotFoundException {
        // 1. 创建一个新文件
        String filename = String.format("%s\\%s.session", SESSION_SAVE, sessionId);
        File file = new File(filename);
        // 2.1 如果文件不存在
        if (!file.exists()) {
            return new HashMap<>();
        }

        // 2.2 如果文件存在
        try (InputStream is = new FileInputStream(file)){
            try (ObjectInputStream oi = new ObjectInputStream(is);){
                return (Map<String, Object>) oi.readObject();
            }
        }
    }

    public void setSessionSave() throws IOException {
        if (sessionData.isEmpty()) {
            return;
        }
        String filename = String.format("%s\\%s.session", SESSION_SAVE, sessionId);

        try(OutputStream outputStream = new FileOutputStream(filename)) {
            try(ObjectOutputStream oot = new ObjectOutputStream(outputStream)) {
                oot.writeObject(sessionData);
                oot.flush();
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        return sessionData.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        sessionData.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        sessionData.put(name, value);
    }
}
