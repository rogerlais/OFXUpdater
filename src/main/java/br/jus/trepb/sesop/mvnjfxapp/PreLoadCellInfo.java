/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.trepb.sesop.mvnjfxapp;

/**
 *
 * @author roger
 */
public class PreLoadCellInfo {

    private final int ddd;
    private final String cellNumber;
    private final String fakeCellNumber;
    private final String description;
    private final String fakeDescription;

    public PreLoadCellInfo(int DDD, String cellNumber, String fakeCellNumber, String description, String fakeDescription) {
        this.ddd = DDD;
        this.cellNumber = cellNumber;
        this.fakeCellNumber = fakeCellNumber;
        this.description = description;
        this.fakeDescription = fakeDescription;
    }

    public String getCellNumber() {
        return this.cellNumber;
    }

    int getDDD() {
        return this.ddd;
    }

    String getFakeDescription() {
        return this.fakeDescription;
    }

    String getFakeCellNumber() {
        return this.fakeCellNumber;
    }

}
