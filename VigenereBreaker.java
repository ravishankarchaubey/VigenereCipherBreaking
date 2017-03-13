import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sliceStr=new StringBuilder();
        for(int i=whichSlice;i<message.length();i+=totalSlices){
            sliceStr.append(message.charAt(i));
        }
        return sliceStr.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for(int i=0;i<klength;++i){
            String s=sliceString(encrypted,i,klength);
            CaesarCracker cc=new CaesarCracker();
            key[i]=cc.getKey(s);
        }
        return key;
    }

    public void breakVigenere () {
        //for breaking of known key length
        /*FileResource fr=new FileResource();
        String encrypted=fr.asString();
        int []key=tryKeyLength(encrypted,4,'e');
        for(int i=0;i<key.length;++i){
            System.out.println(key[i]);
        }
        VigenereCipher vc=new VigenereCipher(key);
        String message=vc.decrypt(encrypted);*/
        
        //for breaking known language and unknown key length
        /*FileResource fr=new FileResource();
        String encrypted=fr.asString();
        FileResource f=new FileResource();
        HashSet<String> dictionary=readDictionary(f);
        String message=breakForLanguage(encrypted,dictionary);
        System.out.println(message);*/
        
        //for breaking unknown key length & language
        FileResource fr=new FileResource();
        String encrypted=fr.asString();
        HashMap<String,HashSet<String>> language=new HashMap<String,HashSet<String>>();
        DirectoryResource dir=new DirectoryResource();
        for(File f: dir.selectedFiles()){
            FileResource res=new FileResource(f);
            HashSet<String> dictionary=readDictionary(res);
            language.put(f.getName(),dictionary);
            System.out.println("Dictionary reading done for "+f.getName());
        }
        breakForAllLangs(encrypted,language);
        
        //only for question on quiz
        /*int []key=tryKeyLength(encrypted,38,'e');
        VigenereCipher vc=new VigenereCipher(key);
        String msg=vc.decrypt(encrypted);
        System.out.println("valid count"+countWords(msg,dictionary));*/
    }
    
    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> dictionary=new HashSet<String>();
        for(String s: fr.lines()){
            s=s.toLowerCase();
            dictionary.add(s);
        }
        return dictionary;
    }
    
    public int countWords(String message,HashSet<String> dictionary){
        int validCounts=0;
        String []words=message.split("\\W+");
        for(String s: words){
            if(dictionary.contains(s.toLowerCase())){
                validCounts++;
            }
        }
        return validCounts;
    }
    
    public String breakForLanguage(String encrypted,HashSet<String> dictionary){
        int []validCounts=new int[101];
        char common=mostCommonCharIn(dictionary);
        for(int i=1;i<101;++i){
            int []key=tryKeyLength(encrypted,i,common);
            VigenereCipher vc=new VigenereCipher(key);
            String message=vc.decrypt(encrypted);
            validCounts[i]=countWords(message,dictionary);
        }
        int max=0;
        for(int i=1;i<101;++i){
            if(validCounts[i]>validCounts[max]){
                max=i;
            }
        }
        //System.out.println("key length:"+max+" max valid count:"+validCounts[max]);
        int []key=tryKeyLength(encrypted,max,'e');
        VigenereCipher vc=new VigenereCipher(key);
        String message=vc.decrypt(encrypted);
        return message;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        String alpha="abcdefghijklmnopqrstuvwxyz";
        int []freqs=new int[26];
        for(String word: dictionary){
            for(int i=0;i<word.length();++i){
                int idx=alpha.indexOf(word.charAt(i));
                if(idx!=-1){
                    freqs[idx]++;
                }
            }
        }
        int maxFreq=0;
        for(int i=0;i<freqs.length;++i){
            if (freqs[i]>freqs[maxFreq]){
                maxFreq=i;
            }
        }
        return alpha.charAt(maxFreq);
    }
    
    public void breakForAllLangs(String encrypted,HashMap<String,HashSet<String>> language){
        HashMap<String,Integer> allLang=new HashMap<String,Integer>();
        for(String lang: language.keySet()){
            String message=breakForLanguage(encrypted,language.get(lang));
            int validCounts=countWords(message,language.get(lang));
            allLang.put(lang,validCounts);
        }
        int max=0;
        String ans="";
        for(String l: allLang.keySet()){
            if(allLang.get(l)>max){
                max=allLang.get(l);
                ans=l;
            }
        }
        String message=breakForLanguage(encrypted,language.get(ans));
        System.out.println("\nEncrypted Language for Message is: "+ans);
        System.out.println("\nDecrypted Message is as follows:\n"+message);
    }
}
