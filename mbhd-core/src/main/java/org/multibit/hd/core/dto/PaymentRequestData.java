package org.multibit.hd.core.dto;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * <p>DTO to provide the following to WalletService:</p>
 * <ul>
 * <li>Additional payment protocol (BIP70) payment request info</li>
 * </ul>
 */
public class PaymentRequestData implements PaymentData {

  /**
   * A UUID used in persisting the BIP70 payment request and as a foreign key to find data from the transaction hash
   */
  private UUID uuid;

  /**
   * The transaction hash of the transaction that paid this payment request.
   * May be absent if the payment request has not been paid yet.
   */
  private Optional<Sha256Hash> transactionHash = Optional.absent();

  /**
   * The fiat payment equivalent of the payment Coin
   */
  private Optional<FiatPayment> fiatPayment = Optional.absent();

  /**
   * The BIP70 PaymentRequest - stored as a file on the file system and provided externally
   */
  private Optional<Protos.PaymentRequest> paymentRequest = Optional.absent();

  /**
   * The BIP70 Payment - stored as a file on the file system when present
   */
  private Optional<Protos.Payment> payment = Optional.absent();

  /**
   * The BIP70 PaymentACK - stored as a file on the file system when present
   */
  private Optional<Protos.PaymentACK> paymentACK = Optional.absent();

  /**
   * The Payment Session Summary wrapping the final state of the BIP70 PaymentSession
   * This provides additional PKI verification information and ensures that the
   * PaymentSession isn't persisted
   */
  private Optional<PaymentSessionSummary> paymentSessionSummary = Optional.absent();

  /**
   * The date of the payment request
   */
  private DateTime date;

  /**
   * The amount in bitcoin of the payment request
   */
  private Coin amountCoin;

  /**
   * The description of the payment request
   */
  private String note = "";

  /**
   * The display name of the PKI identity
   */
  private String identityDisplayName = "";

  /**
   * The trust status - whether the PKI verify was successful
   */
  private PaymentSessionStatus trustStatus;

  /**
   * If the trust status is ERROR or DOWN, a localised text string describing the problem
   */
  private String trustErrorMessage = "";

  /**
   * The expiration date for the payment request
   */
  private DateTime expirationDate;

  /**
   * For protobuf - you probably do not want to use this
   */
  public PaymentRequestData() {
  }

  /**
   * Build the PaymentRequestData from a PaymentSessionSummary
   *
   * @param paymentSessionSummary The Payment Session Summary with a PaymentSession
   */
  public PaymentRequestData(PaymentSessionSummary paymentSessionSummary) {

    this(Optional.of(paymentSessionSummary.getPaymentSession().get().getPaymentRequest()), Optional.<Sha256Hash>absent());

    PaymentSession paymentSession = paymentSessionSummary.getPaymentSession().get();

    setDate(new DateTime(paymentSession.getDate()));
    setExpirationDate(new DateTime(paymentSession.getExpires()));

    setAmountCoin(paymentSession.getValue());
    setNote(paymentSession.getMemo());

    if (paymentSessionSummary.getPkiVerificationData().isPresent()) {
      setIdentityDisplayName(paymentSessionSummary.getPkiVerificationData().get().displayName);
    }

  }

  /**
   * See also the
   * @param paymentRequest  A PaymentRequest
   * @param transactionHash A transaction hash if a Bitcoin transaction has been successfully broadcast
   */
  public PaymentRequestData(Optional<Protos.PaymentRequest> paymentRequest, Optional<Sha256Hash> transactionHash) {
    Preconditions.checkNotNull(paymentRequest);
    Preconditions.checkNotNull(transactionHash);

    this.paymentRequest = paymentRequest;
    this.transactionHash = transactionHash;
    this.uuid = UUID.randomUUID();
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public Optional<Sha256Hash> getTransactionHash() {
    return transactionHash;
  }

  public void setTransactionHash(Optional<Sha256Hash> transactionHash) {
    this.transactionHash = transactionHash;
  }

  public Optional<Protos.PaymentRequest> getPaymentRequest() {
    return paymentRequest;
  }

  public void setPaymentRequest(Optional<Protos.PaymentRequest> paymentRequest) {
    this.paymentRequest = paymentRequest;
  }

  public void setPayment(Optional<Protos.Payment> payment) {
    this.payment = payment;
  }

  public void setPaymentACK(Optional<Protos.PaymentACK> paymentACK) {
    this.paymentACK = paymentACK;
  }

  @Override
  public PaymentType getType() {
    if (transactionHash.isPresent()) {
      return PaymentType.PAID;
    } else {
      return PaymentType.THEY_REQUESTED;
    }
  }

  @Override
  public PaymentStatus getStatus() {
    if (transactionHash.isPresent()) {
      return new PaymentStatus(RAGStatus.GREEN, CoreMessageKey.PAYMENT_PAID);
    } else {
      return new PaymentStatus(RAGStatus.PINK, CoreMessageKey.PAYMENT_REQUESTED_BY_THEM);
    }
  }

  @Override
  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  @Override
  public Coin getAmountCoin() {
    return amountCoin;
  }

  public void setAmountCoin(Coin amountBTC) {
    this.amountCoin = amountBTC;
  }

  public void setAmountFiat(FiatPayment fiatPayment) {
    this.fiatPayment = Optional.of(fiatPayment);
  }

  @Override
  public FiatPayment getAmountFiat() {
    if (fiatPayment.isPresent()) {
      return fiatPayment.get();
    } else {
      return new FiatPayment();
    }
  }

  @Override
  public String getNote() {
    return note;
  }

  @Override
  public String getDescription() {
    return getNote();
  }

  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public boolean isCoinBase() {
    return false;
  }

  public String getIdentityDisplayName() {
    return identityDisplayName;
  }

  public void setIdentityDisplayName(String identityDisplayName) {
    this.identityDisplayName = identityDisplayName;
  }

  public PaymentSessionStatus getTrustStatus() {
    return trustStatus;
  }

  public void setTrustStatus(PaymentSessionStatus trustStatus) {
    this.trustStatus = trustStatus;
  }

  public String getTrustErrorMessage() {
    return trustErrorMessage;
  }

  public void setTrustErrorMessage(String trustErrorMessage) {
    this.trustErrorMessage = trustErrorMessage;
  }

  public DateTime getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(DateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Optional<FiatPayment> getFiatPayment() {
    return fiatPayment;
  }

  public Optional<PaymentSessionSummary> getPaymentSessionSummary() {
    return paymentSessionSummary;
  }

  /**
   * @return The BIP70 Payment sent to the server once payment was successfully broadcast
   */
  public Optional<Protos.Payment> getPayment() {
    return payment;
  }

  /**
   * @return The BIP70 PaymentACK received from the server
   */
  public Optional<Protos.PaymentACK> getPaymentACK() {
    return paymentACK;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PaymentRequestData that = (PaymentRequestData) o;

    if (!uuid.equals(that.uuid)) {
      return false;
    }
    if (!transactionHash.equals(that.transactionHash)) {
      return false;
    }
    if (!fiatPayment.equals(that.fiatPayment)) {
      return false;
    }
    if (!paymentRequest.equals(that.paymentRequest)) {
      return false;
    }
    if (!payment.equals(that.payment)) {
      return false;
    }
    if (!paymentACK.equals(that.paymentACK)) {
      return false;
    }
    if (!paymentSessionSummary.equals(that.paymentSessionSummary)) {
      return false;
    }
    if (!date.equals(that.date)) {
      return false;
    }
    if (!amountCoin.equals(that.amountCoin)) {
      return false;
    }
    if (!note.equals(that.note)) {
      return false;
    }
    if (!identityDisplayName.equals(that.identityDisplayName)) {
      return false;
    }
    if (trustStatus != that.trustStatus) {
      return false;
    }
    if (!trustErrorMessage.equals(that.trustErrorMessage)) {
      return false;
    }
    return expirationDate.equals(that.expirationDate);

  }

  @Override
  public int hashCode() {
    int result = uuid.hashCode();
    result = 31 * result + transactionHash.hashCode();
    result = 31 * result + fiatPayment.hashCode();
    result = 31 * result + paymentRequest.hashCode();
    result = 31 * result + payment.hashCode();
    result = 31 * result + paymentACK.hashCode();
    result = 31 * result + paymentSessionSummary.hashCode();
    result = 31 * result + date.hashCode();
    result = 31 * result + amountCoin.hashCode();
    result = 31 * result + note.hashCode();
    result = 31 * result + identityDisplayName.hashCode();
    result = 31 * result + trustStatus.hashCode();
    result = 31 * result + trustErrorMessage.hashCode();
    result = 31 * result + expirationDate.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "PaymentRequestData{" +
      "amountBTC=" + amountCoin +
      ", uuid=" + uuid +
      ", transactionHash=" + transactionHash +
      ", fiatPayment=" + fiatPayment +
      ", paymentRequest=" + paymentRequest +
      ", payment=" + payment +
      ", paymentACK=" + paymentACK +
      ", paymentSessionSummary=" + paymentSessionSummary +
      ", date=" + date +
      ", note='" + note + '\'' +
      ", identityDisplayName='" + identityDisplayName + '\'' +
      ", trustStatus=" + trustStatus +
      ", trustErrorMessage='" + trustErrorMessage + '\'' +
      ", expirationDate=" + expirationDate +
      '}';
  }
}
