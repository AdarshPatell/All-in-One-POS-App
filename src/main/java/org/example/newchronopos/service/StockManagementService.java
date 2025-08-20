package org.example.newchronopos.service;

import org.example.newchronopos.dao.StockAdjustmentDAO;
import org.example.newchronopos.dao.StockTransferDAO;
import org.example.newchronopos.dao.GoodsReceivedDAO;
import org.example.newchronopos.dao.GoodsReplacedDAO;
import org.example.newchronopos.dao.GoodsReturnDAO;
import org.example.newchronopos.model.*;

import java.util.List;

public class StockManagementService {
    
    private final StockAdjustmentDAO stockAdjustmentDAO;
    private final StockTransferDAO stockTransferDAO;
    private final GoodsReceivedDAO goodsReceivedDAO;
    private final GoodsReplacedDAO goodsReplacedDAO;
    private final GoodsReturnDAO goodsReturnDAO;

    public StockManagementService() {
        this.stockAdjustmentDAO = new StockAdjustmentDAO();
        this.stockTransferDAO = new StockTransferDAO();
        this.goodsReceivedDAO = new GoodsReceivedDAO();
        this.goodsReplacedDAO = new GoodsReplacedDAO();
        this.goodsReturnDAO = new GoodsReturnDAO();
    }

    // Stock Adjustment Services
    public boolean createStockAdjustment(StockAdjustment adjustment) {
        // Generate adjustment number if not provided
        if (adjustment.getAdjustmentNo() == null || adjustment.getAdjustmentNo().isEmpty()) {
            adjustment.setAdjustmentNo(stockAdjustmentDAO.generateAdjustmentNumber());
        }
        
        boolean success = stockAdjustmentDAO.createStockAdjustment(adjustment);
        if (success) {
            // Log stock movement for audit trail
            logStockMovement(adjustment);
        }
        return success;
    }

    public List<StockAdjustment> getAllStockAdjustments() {
        return stockAdjustmentDAO.getAllStockAdjustments();
    }

    public boolean updateStockAdjustment(StockAdjustment adjustment) {
        return stockAdjustmentDAO.updateStockAdjustment(adjustment);
    }

    // Stock Transfer Services
    public boolean createStockTransfer(StockTransfer transfer) {
        if (transfer.getTransferNo() == null || transfer.getTransferNo().isEmpty()) {
            transfer.setTransferNo(stockTransferDAO.generateTransferNumber());
        }
        
        boolean success = stockTransferDAO.createStockTransfer(transfer);
        if (success) {
            // Create stock movements for both locations
            logStockTransferMovement(transfer);
        }
        return success;
    }

    public List<StockTransfer> getAllStockTransfers() {
        return stockTransferDAO.getAllStockTransfers();
    }

    // Goods Received Services
    public boolean createGoodsReceived(GoodsReceived goods) {
        if (goods.getReceivedNo() == null || goods.getReceivedNo().isEmpty()) {
            goods.setReceivedNo(goodsReceivedDAO.generateReceivedNumber());
        }
        
        boolean success = goodsReceivedDAO.createGoodsReceived(goods);
        if (success) {
            // Log stock increase
            logGoodsReceivedMovement(goods);
        }
        return success;
    }

    public List<GoodsReceived> getAllGoodsReceived() {
        return goodsReceivedDAO.getAllGoodsReceived();
    }

    // Goods Replaced Services
    public boolean createGoodsReplaced(GoodsReplaced replaced) {
        if (replaced.getReplacedNo() == null || replaced.getReplacedNo().isEmpty()) {
            replaced.setReplacedNo(goodsReplacedDAO.generateReplacedNumber());
        }
        
        return goodsReplacedDAO.createGoodsReplaced(replaced);
    }

    public List<GoodsReplaced> getAllGoodsReplaced() {
        return goodsReplacedDAO.getAllGoodsReplaced();
    }

    // Goods Return Services
    public boolean createGoodsReturn(GoodsReturn returnItem) {
        if (returnItem.getReturnNo() == null || returnItem.getReturnNo().isEmpty()) {
            returnItem.setReturnNo(goodsReturnDAO.generateReturnNumber());
        }
        
        boolean success = goodsReturnDAO.createGoodsReturn(returnItem);
        if (success) {
            // Log stock decrease for returns
            logGoodsReturnMovement(returnItem);
        }
        return success;
    }

    public List<GoodsReturn> getAllGoodsReturn() {
        return goodsReturnDAO.getAllGoodsReturn();
    }

    // Private helper methods for stock movement logging
    private void logStockMovement(StockAdjustment adjustment) {
        // TODO: Implement stock movement logging
        System.out.println("Stock movement logged for adjustment: " + adjustment.getAdjustmentNo());
    }

    private void logStockTransferMovement(StockTransfer transfer) {
        // TODO: Implement stock transfer movement logging
        System.out.println("Stock transfer movement logged: " + transfer.getTransferNo());
    }

    private void logGoodsReceivedMovement(GoodsReceived goods) {
        // TODO: Implement goods received movement logging
        System.out.println("Goods received movement logged: " + goods.getReceivedNo());
    }

    private void logGoodsReturnMovement(GoodsReturn returnItem) {
        // TODO: Implement goods return movement logging
        System.out.println("Goods return movement logged: " + returnItem.getReturnNo());
    }

    // Utility methods
    public String generateAdjustmentNumber() {
        return stockAdjustmentDAO.generateAdjustmentNumber();
    }

    public String generateTransferNumber() {
        return stockTransferDAO.generateTransferNumber();
    }

    public String generateReceivedNumber() {
        return goodsReceivedDAO.generateReceivedNumber();
    }

    public String generateReplacedNumber() {
        return goodsReplacedDAO.generateReplacedNumber();
    }

    public String generateReturnNumber() {
        return goodsReturnDAO.generateReturnNumber();
    }
}
