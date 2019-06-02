package kin.sdk;

import kin.base.KeyPair;
import kin.base.Memo;
import kin.base.Operation;
import kin.base.TimeBounds;
import kin.base.Transaction;
import kin.base.Transaction.Builder;
import kin.base.TransactionBuilderAccount;

public class TransactionBuilder {

    private final Builder builder;
    private final KeyPair account;

    /**
     * Construct a new transaction builder.
     *
     * @param account The source account for this transaction. This account is the account
     * @param sourceAccount The source account for this transaction. This account is the account who will use a sequence
     * number. When build() is called, the account object's sequence number will be incremented.
     */
    TransactionBuilder(KeyPair account, TransactionBuilderAccount sourceAccount) {
        this.account = account;
        builder = new Builder(sourceAccount);
    }

    public int getOperationsCount() {
        return builder.getOperationsCount();
    }

    /**
     * Adds a new <a href="https://www.stellar.org/developers/learn/concepts/list-of-operations.html"
     * target="_blank">operation</a> to this transaction.
     *
     * @return Builder object so you can chain methods.
     * @see Operation
     */
    public TransactionBuilder addOperation(Operation operation) {
        builder.addOperation(operation);
        return this;
    }

    /**
     * @param fee this transaction fee
     * @return Builder object so you can chain methods.
     */
    public TransactionBuilder setFee(int fee) {
        builder.addFee(fee);
        return this;
    }

    /**
     * Adds a <a href="https://www.stellar.org/developers/learn/concepts/transactions.html" target="_blank">memo</a> to
     * this transaction.
     *
     * @param appId is a 3-4 character string which is added to each transaction memo to identify your application.
     * appId must contain only digits and upper and/or lower case letters. String length must be 3 or 4.
     * @return Builder object so you can chain methods.
     * @see Memo
     */
    public TransactionBuilder setMemo(String appId, String memo) {
        builder.addMemo(Memo.text(Utils.addAppIdToMemo(appId, memo)));
        // TODO: 2019-05-23 should we validate memo like we are doing in transaction sender class?
        return this;
    }

    /**
     * Adds a <a href="https://www.stellar.org/developers/learn/concepts/transactions.html"
     * target="_blank">time-bounds</a> to this transaction.
     *
     * @return Builder object so you can chain methods.
     * @see TimeBounds
     */
    public TransactionBuilder setTimeBounds(TimeBounds timeBounds) {
        builder.addTimeBounds(timeBounds);
        return this;
    }

    /**
     * Builds a transaction. It will increment sequence number of the source account.
     */
    public kin.sdk.Transaction build() {
        Transaction baseTransaction = builder.build();
        TransactionIdImpl transactionId = new TransactionIdImpl(Utils.byteArrayToHex(baseTransaction.hash()));
        baseTransaction.sign(account);
        return new kin.sdk.Transaction(transactionId, baseTransaction);
    }

}