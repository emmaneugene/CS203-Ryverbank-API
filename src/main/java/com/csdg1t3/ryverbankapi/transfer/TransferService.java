package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;

public interface TransferService {
    List<Transfer> listTransfer();
    Transfer getTransfer(Long id);

    /**
     * Return newly added transfer
     */
    Transfer addTransfer(Transfer transfer);
}