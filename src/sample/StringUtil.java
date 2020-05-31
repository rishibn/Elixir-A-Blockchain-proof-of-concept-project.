package sample;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.List;

public class StringUtil {

    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Applies ECDSA Signature and returns the result ( as bytes ).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }


    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }


    //This function returns the String formed
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


    //This function is used to return the MerkleRoot
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }


    //This function is used to generate the specific nuber of wallets as per user desires.
    public static void generateWallets(int numWalletsSliderValue){
        for(int i = 0; i < numWalletsSliderValue; i++){
            MainPageController.wallets.add(new Wallet());
        }
    }


    //This function return the complete summary of the generated Blockchain.
    public static String printBlockChain(){
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------- BLOCKCHAIN ELIXIR -------------------------\n");
        sb.append("\n{");
        for(Block b : MainPageController.blockchain){
            if(b.previousHash.equalsIgnoreCase("0")) continue;
            sb.append("\n\t--------------------------------------------------------------------------------------------");
            sb.append("\n\t{");
            sb.append("\n\t hash : ").append(b.hash);
            sb.append("\n\t previousHash : ").append(b.previousHash);
            sb.append("\n\t merkleRoot : ").append(b.merkleRoot);
            sb.append("\n\t timeStamp : ").append(b.timeStamp);
            sb.append("\n\t nonce : ").append(b.nonce);
            if(b.transactions.size()==0){
                sb.append("\n\t transactions : ").append("[]");
            }
            else{
                sb.append("\n\t transactions : ").append("[");
                for(Transaction t: b.transactions){
                    sb.append("\n\t\t transactionId : ").append(t.transactionId);
                    sb.append("\n\t\t senderKey : ").append(getStringFromKey(t.sender));
                    sb.append("\n\t\t reciepientKey : ").append(getStringFromKey(t.reciepient));
                    sb.append("\n\t\t value : ").append(t.value);
                    sb.append("\n\t\t signature : ").append(Base64.getEncoder().encodeToString(t.signature));
                    if(t.inputs.size() == 0) {
                        sb.append("\n\t\t inputs : ").append("[]");
                    }
                    else{
                        sb.append("\n\t\t inputs : ").append("{");
                        for(TransactionInput ti : t.inputs) {

                            sb.append("\n\t\t\t recieved from ID : ").append(ti.transactionOutputId);
                            sb.append("\n\t\t\t UTXO : ").append("{");
                            sb.append("\n\t\t\t\t id :").append(ti.UTXO.id);
                            sb.append("\n\t\t\t\t reciepient :").append(getStringFromKey(ti.UTXO.reciepient));
                            sb.append("\n\t\t\t\t value :").append(ti.UTXO.value);
                            sb.append("\n\t\t\t\t parentTransactionId :").append(ti.UTXO.parentTransactionId);
                            sb.append("\n\t\t\t}");

                        }
                        sb.append("\n\t\t}");
                    }
                    if(t.outputs.size() == 0) {
                        sb.append("\n\t\t outputs : ").append("[]");
                    }
                    else {
                        sb.append("\n\t\t outputs : ").append("{");
                        for (TransactionOutput to : t.outputs) {

                            sb.append("\n\t\t\t{");
                            sb.append("\n\t\t\t\t id :").append(to.id);
                            sb.append("\n\t\t\t\t reciepient :").append(getStringFromKey(to.reciepient));
                            sb.append("\n\t\t\t\t value :").append(to.value);
                            sb.append("\n\t\t\t\t parentTransactionId : ").append(to.parentTransactionId);
                            sb.append("\n\t\t\t}");
                        }
                        sb.append("\n\t\t}");
                    }
                }
                sb.append("\n\t]");
            }
            sb.append("\n\t}");
            sb.append("\n\t--------------------------------------------------------------------------------------------");

        }
        sb.append("\n}");
        return sb.toString();
    }


    //This function return the complete summary of the Invalid transactions that occured during the transactions processing.
    public static String getInvalidTransactions(){
        StringBuilder sb = new StringBuilder();
        int transactionNum = 0;
        sb.append("\n\t------------------------------ INVALID TRANSACTIONS ---------------------------------------\n");
        for(Block b : MainPageController.blockchain){

            if(b.transactions.isEmpty()){
                sb.append("\n\t------------------------------ TRANSACTION : "+ transactionNum +"---------------------------------------------");
                sb.append("\n\t{");
                sb.append("\n\t hash : ").append(b.hash);
                sb.append("\n\t previousHash : ").append(b.previousHash);
                sb.append("\n\t merkleRoot : ").append(b.merkleRoot);
                sb.append("\n\t timeStamp : ").append(b.timeStamp);
                sb.append("\n\t nonce : ").append(b.nonce);
                sb.append("\n\t transactions : ").append("[]");
                sb.append("\n\t--------------------------------------------------------------------------------------------");
            }
            transactionNum++;
        }
        return sb.toString();
    }

}