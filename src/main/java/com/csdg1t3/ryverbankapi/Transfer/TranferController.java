@RestController
public class TransferController {
    private TransferRepository tranfers;
    // private AccountRepository accounts;

    public TransferController(TransferRepository transfers /*, AccountRepository accounts*/) {
        this.transfers = transfers;
        // this.accounts = accounts;
    }

    @PostMapping("/accountURL/{account_id}/transactions")
    public Transfer addTransfer(@Valid @PathVariable (value = "account_id") Long accountId, @Valid @RequestBody Transfer trasnfer) {
        
    }
}