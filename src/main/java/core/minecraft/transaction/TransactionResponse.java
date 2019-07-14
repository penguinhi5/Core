package core.minecraft.transaction;

/**
 * These are all of the responses that can be returned in a transaction
 *
 * @author Preston Brown
 */
public enum TransactionResponse {

    SUCCESSFUL,
    FAILED,
    ALREADY_OWNED,
    INSUFFICIENT_FUNDS;
}
