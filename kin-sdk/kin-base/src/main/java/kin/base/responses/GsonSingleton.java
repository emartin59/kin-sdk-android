package kin.base.responses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import kin.base.Asset;
import kin.base.KeyPair;
import kin.base.responses.effects.EffectResponse;
import kin.base.responses.operations.OperationResponse;

public class GsonSingleton {
    private static Gson instance = null;

    protected GsonSingleton() {
    }

    public static Gson getInstance() {
        if (instance == null) {
            TypeToken accountPageType = new TypeToken<Page<AccountResponse>>() {
            };
            TypeToken effectPageType = new TypeToken<Page<EffectResponse>>() {
            };
            TypeToken ledgerPageType = new TypeToken<Page<LedgerResponse>>() {
            };
            TypeToken offerPageType = new TypeToken<Page<OfferResponse>>() {
            };
            TypeToken operationPageType = new TypeToken<Page<OperationResponse>>() {
            };
            TypeToken pathPageType = new TypeToken<Page<PathResponse>>() {
            };
            TypeToken tradePageType = new TypeToken<Page<TradeResponse>>() {
            };
            TypeToken transactionPageType = new TypeToken<Page<TransactionResponse>>() {
            };

            instance = new GsonBuilder()
                    .registerTypeAdapter(Asset.class, new AssetDeserializer())
                    .registerTypeAdapter(KeyPair.class, new KeyPairTypeAdapter().nullSafe())
                    .registerTypeAdapter(OperationResponse.class, new OperationDeserializer())
                    .registerTypeAdapter(EffectResponse.class, new EffectDeserializer())
                    .registerTypeAdapter(TransactionResponse.class, new TransactionDeserializer())
                    .registerTypeAdapter(accountPageType.getType(), new PageDeserializer<AccountResponse>(accountPageType))
                    .registerTypeAdapter(effectPageType.getType(), new PageDeserializer<AccountResponse>(effectPageType))
                    .registerTypeAdapter(ledgerPageType.getType(), new PageDeserializer<LedgerResponse>(ledgerPageType))
                    .registerTypeAdapter(offerPageType.getType(), new PageDeserializer<OfferResponse>(offerPageType))
                    .registerTypeAdapter(operationPageType.getType(), new PageDeserializer<OperationResponse>(operationPageType))
                    .registerTypeAdapter(pathPageType.getType(), new PageDeserializer<PathResponse>(pathPageType))
                    .registerTypeAdapter(tradePageType.getType(), new PageDeserializer<TradeResponse>(tradePageType))
                    .registerTypeAdapter(transactionPageType.getType(), new PageDeserializer<TransactionResponse>(transactionPageType))
                    .create();
        }
        return instance;
    }

}
