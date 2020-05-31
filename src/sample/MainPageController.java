package sample;

//import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;


public class MainPageController implements Initializable {

    //Registering controllers
    @FXML
    Button startTransactionsButton, blockchainSummaryButton, invalidTransactionsButton, resetButton;
    @FXML
    TextArea outputTextarea, counterTextArea;
    @FXML
    MenuItem aboutButton;
    @FXML
    ComboBox<Integer> difficultyComboBox;
    @FXML
    Slider transactionSlider, walletSlider;


    //Declaring Lists and Hashmaps
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static ArrayList<Wallet> wallets = new ArrayList<>();

    //Declaring static variables to be used
    public static int difficulty, transactionSliderValue, walletSliderValue;
    public static float minimumTransaction = 0.1f;
    public static Transaction genesisTransaction;

    //Declaring Strings for the Main and other TextArea as well as
    //counters for valid and invalid transactions.
    //outputString will be used to alter the text in the Main TextArea of ELIXIR Window.
    //counterString will be used to alter the small window showing the counter values.
    String outputString = "";
    String counterString ="";
    int invalidCounter = 0;
    int validCounter = 1;

    //To switch the layouts
    Stage stage = new Stage();
    Scene scene;
    Thread th;

    //Initializing the main window
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDifficultyComboBox();
        blockchainSummaryButton.setDisable(true);
        invalidTransactionsButton.setDisable(true);
        resetButton.setDisable(true);
    }

    //Setting the values inside the ComboBox on intialization
    public void setDifficultyComboBox(){
        System.out.println("INITIALIZED.");
        int difficulty_level_2 = 2;
        int difficulty_level_3 = 3;
        int difficulty_level_4 = 4;
        int difficulty_level_5 = 5;
        int difficulty_level_6 = 6;
        int difficulty_level_7 = 7;
        difficultyComboBox.getItems().addAll(difficulty_level_2,difficulty_level_3,difficulty_level_4,difficulty_level_5,difficulty_level_6,difficulty_level_7);
        difficultyComboBox.setValue(difficulty_level_4);
        difficultyComboBox.setEditable(false);
    }


    //This button launches a new window showing the about section of the project.
    public void aboutButtonClicked(){
        System.out.println("About Button Clicked");
        AboutBox.display("About","ELIXIR: A Blockchain proof of concept project.\nMade by Arun Kumar and B N Rishi." +
                "\n");
    }


    //This is tha most important function which is activated when the user desires to
    //start the processing of the transactions after selecting specific parameters.
    public void startTransactionsButtonClicked(){

        System.out.println("Start Transactions Button Clicked!");

        //Disabling the Start Transactions button until all transactions finish processing.
        startTransactionsButton.setDisable(true);

        //Setting up Bounty Castle as Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Getting the values of the specif parameters selected by the user.
        difficulty = difficultyComboBox.getValue();
        transactionSliderValue = (int) transactionSlider.getValue();
        walletSliderValue = (int) walletSlider.getValue();


        //Using a thread for concurrency control
        th = new Thread(() ->{

            System.out.println("Generate button clicked.");
            outputTextarea.appendText("<------------WELCOME TO ELIXIR------------>\n");

            counterString = "Total transactions: "+transactionSliderValue+"\n\n"
                    +"Valid transactions: 0\n\n"
                    +"Invalid transactions: 0";
            counterTextArea.setText(counterString);

            outputString = "\nDifficulty selected: "+difficulty+"\n";
            outputTextarea.appendText(outputString);
            System.out.println("Difficulty Selected: "+difficulty+"\n");

            System.out.println("Number of transactions selected: "+transactionSliderValue+"\n");
            outputTextarea.appendText("Number of transactions selected: "+transactionSliderValue+"\n");


            System.out.println("Number of wallets selected: "+walletSliderValue+"\n");
            outputTextarea.appendText("Number of wallets selected: "+walletSliderValue+"\n");

            outputTextarea.appendText("\n<------------STARTING TRANSACTIONS------------>\n");

            try {
                Thread.sleep(225);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Create wallets:
            Wallet coinbase = new Wallet();
            StringUtil.generateWallets(walletSliderValue);

            //create genesis transaction, which sends 1000 Elixir to walletA:
            genesisTransaction = new Transaction(coinbase.publicKey, wallets.get(0).publicKey, 1000f, null);
            genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
            genesisTransaction.transactionId = "0"; //manually set the transaction id
            genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
            UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.


            //Mining the very first block for the genesis transaction and adding it to our blockchain.
            outputTextarea.appendText("\nCreating and Mining Genesis block... ");
            Block genesis = new Block("0");
            genesis.addTransaction(genesisTransaction);
            addBlock(genesis);


            //The wallets will be selected at random to perform the transactions.
            Random rand = new Random();
            int numWalletsSliderValue = wallets.size();
            String previousBlockHash = blockchain.get(0).hash;
            for(int i=0; i<transactionSliderValue; i++){
                int walletAid = rand.nextInt(numWalletsSliderValue);
                int walletBid = rand.nextInt(numWalletsSliderValue);
                if(i == 0) walletAid = 0;
                while(walletAid == walletBid ){
                    walletBid = rand.nextInt(numWalletsSliderValue);
                }

                //The funds will also be selected at random in the range of 100.
                int fund = rand.nextInt(100);
                while(fund == 0){ fund = rand.nextInt(20);}

                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                outputTextarea.appendText("\n\n------------------------- Transaction : "+ (i+1) +" -------------------------------------\n");
                Block block = new Block(previousBlockHash);
                //System.out.println("\nWallet_"+ walletAid +"'s balance is: " + wallets.get(walletAid).getBalance());
                outputTextarea.appendText("\n\nWallet_"+ walletAid +"'s balance is: " + wallets.get(walletAid).getBalance());
                //System.out.println("\nWallet_"+walletAid +" is Attempting to send "+fund+" elxr to Wallet_"+ walletBid+": ");
                outputTextarea.appendText("\nWallet_"+walletAid +" is Attempting to send "+fund+" elxr to Wallet_"+ walletBid+": \n");

                try{
                    boolean res = block.addTransaction(wallets.get(walletAid).sendFunds( wallets.get(walletBid).publicKey, fund));
                    if(!res){
                        invalidCounter += 1;
                        outputTextarea.appendText("\n #Not Enough funds to send transaction. Transaction Discarded.\n");
                    }


                    addBlock(block);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                //Initializing the counterTextArea wil initial String.
                validCounter = (i+1) - invalidCounter;
                counterString = "Total transactions: "+transactionSliderValue+"\n\n"
                        +"Valid transactions: "+validCounter+"\n\n"
                        +"Invalid transactions: "+invalidCounter;
                counterTextArea.setText(counterString);


                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                outputTextarea.appendText("\nWallet_"+ walletAid +"'s balance is: " + wallets.get(walletAid).getBalance());
                outputTextarea.appendText("\nWallet_" + walletBid +"'s balance is: " + wallets.get(walletBid).getBalance());
                previousBlockHash = block.hash;
                outputTextarea.appendText("\n--------------------------------------------------------------------------------\n");

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(i == transactionSliderValue - 1) counterTextArea.appendText("\n\n### THE END ###");
            }


            try {
                Thread.sleep(125);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            outputTextarea.appendText("\n\n<------------TRANSACTIONS EXECUTED SUCCESSFULLY.------------>\n");
            outputTextarea.appendText("\n\n<------------BLOCKCHAIN VALIDITY.------------>\n\n");

            //Checking if the generated blockchain is valid or not and displaying the same.
            boolean res = isChainValid();
            if(res)
                outputTextarea.appendText("VALIDITY OF GENERATED BLOCKCHAIN: TRUE");
            else
                outputTextarea.appendText("VALIDITY OF GENERATED BLOCKCHAIN: FALSE");
            outputTextarea.appendText("\n\n<------------BLOCKCHAIN VALIDITY.------------>\n");

        });
        th.setDaemon(true);
        th.start();

        //Enabling the buttons for post analysis of transactions performed.
        blockchainSummaryButton.setDisable(false);
        invalidTransactionsButton.setDisable(false);
        resetButton.setDisable(false);
    }


    //This Listener launches a new window containing the Blockchain summary of the transactions performed.
    public void blockchainSummaryButtonClicked(){
        System.out.println("Blockchain Summary Button Clicked.");
        //System.out.println(StringUtil.printBlockChain());
        try{
            scene = new Scene(FXMLLoader.load(getClass().getResource("BlockchainSummaryPage.fxml")));
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }


    //This Listener launched a new window containing information about all the invalid transactions that occured
    //during the transaction processing.
    public void invalidTransactionsButtonClicked(){
        System.out.println("Invalid transactions button clicked.");
        try{
            scene = new Scene(FXMLLoader.load(getClass().getResource("InvalidTransactionPage.fxml")));
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }


    //This Listener resets the complete UI to start a new.
    public void resetButtonClicked(){
        System.out.println("Reset button clicked.");
        blockchain.clear();
        UTXOs.clear();
        wallets.clear();
        outputTextarea.clear();
        counterTextArea.clear();
        invalidCounter = 0;
        validCounter = 0;
        th.stop();
        startTransactionsButton.setDisable(false);
        blockchainSummaryButton.setDisable(true);
        invalidTransactionsButton.setDisable(true);
        resetButton.setDisable(true);
    }


    //This performs the check on the generated Blockchain and determines it's validity.
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Validity of Blockchain: Blockchain is valid.");

        return true;
    }


    //This function is used to add a new block to the blockchain ArrayList
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

}
