package com.leon.domain.aggregate;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
public class SpeechContext {

    private final static String Period = "。";
    private final static int MAX_LEN = 510;
    private static final Set<Character> SPLIT_BY = Set.of(',', '.', '?', '，', '。', '？');

    private List<Chat> chats;

    private Integer audioSample;

    @Data
    public static class Chat {

        private String text;

        private String person;

        private String speed;

        private String pitch;

        private String volume;

        boolean isSame(Chat chat) {
            return chat != null && this.person.equals(chat.person);
        }

        void merge(Chat chat) {
            text = text + Period + chat.text;
        }

        boolean notOverLength() {
            return text.length() <= MAX_LEN;
        }
    }

    public int getConsumeWords() {
        int sum = 0;
        if (CollectionUtils.isEmpty(chats)) {
            return sum;
        }
        for (Chat chat : chats) {
            sum += chat.text.length();
        }
        return sum;
    }

    public void optimize() {
        mergeSamePerson();

        LinkedList<Chat> optimized = new LinkedList<>();

        for (Chat chat : chats) {
            while (true) {
                if (chat.notOverLength()) {
                    optimized.add(chat);
                    break;
                }
                char[] chars = chat.text.toCharArray();
                for (int i = MAX_LEN; i > 0 ; i--) {
                    if (SPLIT_BY.contains(chars[i])) {
                        Chat newChat = new Chat();
                        BeanUtils.copyProperties(chat, newChat);
                        newChat.text = chat.text.substring(0, i);
                        chat.text = chat.text.substring(i + 1);
                        optimized.add(newChat);
                        break;
                    }
                }
            }
        }
        chats = optimized;
    }

    public void mergeSamePerson() {
        LinkedList<Chat> merged = new LinkedList<>();
        merged.add(chats.get(0));

        for (Chat chat : chats) {
            if (chat == merged.getLast()) continue;

            if (chat.isSame(merged.getLast())) {
                Chat latest = merged.getLast();
                latest.merge(chat);
            } else {
                merged.add(chat);
            }
        }
        chats = merged;
    }

    public static void main(String[] args) {
        SpeechContext speechContext = new SpeechContext();
        Chat chat1 = new Chat();
        chat1.setText("你是谁，我是你爹哈");
        chat1.setPerson("1001");
        Chat chat2 = new Chat();
        chat2.setText("还是同一个人，这把长一点，我就不信了，怎么说哈哈哈。");
        chat2.setPerson("1001");

        Chat chat3 = new Chat();
        chat3.setText("另一个人了");
        chat3.setPerson("1002");

        Chat chat4 = new Chat();
        chat4.setText("还是同一个人，但不在一起，还阿斯蒂芬是同个人，但不在一起");
        chat4.setPerson("1001");

        Chat chat5 = new Chat();
        chat5.setText("耶");
        chat5.setPerson("1001");

        speechContext.setChats(List.of(chat1, chat2, chat3, chat4, chat5));

        speechContext.optimize();

        List<Chat> a = speechContext.getChats();
        System.out.println();
    }

}
