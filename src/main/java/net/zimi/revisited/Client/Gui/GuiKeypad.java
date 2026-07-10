//package net.zimi.revisited.Client.Gui;
//
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.GuiTextField;
//import net.zimi.revisited.Addon;
//import org.lwjgl.input.Keyboard;
//
//public class GuiKeypad extends GuiScreen {
//    private GuiTextField codeField;
//    private GuiTextField targetXField;
//    private GuiTextField targetZField;
//    private GuiButton confirmButton;
//    private int blockX, blockY, blockZ;
//
//    public GuiKeypad(int x, int y, int z) {
//        this.blockX = x; this.blockY = y; this.blockZ = z;
//    }
//
//    @Override
//    public void initGui() {
//        Keyboard.enableRepeatEvents(true);
//        this.buttonList.clear();
//
//        this.codeField = new GuiTextField(this.fontRendererObj, this.width / 2 - 50, this.height / 2 - 45, 100, 20);
//        this.codeField.setMaxStringLength(8);
//        this.codeField.setFocused(true);
//
//        this.targetXField = new GuiTextField(this.fontRendererObj, this.width / 2 - 80, this.height / 2 - 5, 75, 20);
//        this.targetXField.setMaxStringLength(5);
//
//        this.targetZField = new GuiTextField(this.fontRendererObj, this.width / 2 + 5, this.height / 2 - 5, 75, 20);
//        this.targetZField.setMaxStringLength(5);
//
//        this.confirmButton = new GuiButton(0, this.width / 2 - 60, this.height / 2 + 30, 120, 20, "CONFIRM LAUNCH");
//        this.confirmButton.enabled = false;
//        this.buttonList.add(this.confirmButton);
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button) {
//        if (button.id == 0) {
//            String code = this.codeField.getText();
//            try {
//                int targetX = Integer.parseInt(this.targetXField.getText());
//                int targetZ = Integer.parseInt(this.targetZField.getText());
//
//                Addon.Network.PACKET.sendToServer(new Addon.Network.Message_LaunchNukeC2S(code, blockX, blockY, blockZ, targetX, targetZ));
//                this.mc.displayGuiScreen(null);
//            } catch (NumberFormatException e) {
//            }
//        }
//    }
//
//    @Override
//    protected void keyTyped(char typedChar, int keyCode) {
//        // Permettiamo numeri, BACKSPACE e il segno MENO (-) per le coordinate negative
//        if (Character.isDigit(typedChar) || keyCode == Keyboard.KEY_BACK || typedChar == '-') {
//            if (this.codeField.isFocused() && typedChar != '-') { // Il codice non può avere il meno
//                this.codeField.textboxKeyTyped(typedChar, keyCode);
//            } else if (this.targetXField.isFocused()) {
//                this.targetXField.textboxKeyTyped(typedChar, keyCode);
//            } else if (this.targetZField.isFocused()) {
//                this.targetZField.textboxKeyTyped(typedChar, keyCode);
//            }
//        }
//
//        // Tasto TAB per passare da una casella all'altra rapidamente!
//        if (keyCode == Keyboard.KEY_TAB) {
//            if (this.codeField.isFocused()) {
//                this.codeField.setFocused(false);
//                this.targetXField.setFocused(true);
//            } else if (this.targetXField.isFocused()) {
//                this.targetXField.setFocused(false);
//                this.targetZField.setFocused(true);
//            } else {
//                this.targetZField.setFocused(false);
//                this.codeField.setFocused(true);
//            }
//        }
//
//        super.keyTyped(typedChar, keyCode);
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//        // Passiamo i click a tutte le caselle
//        this.codeField.mouseClicked(mouseX, mouseY, mouseButton);
//        this.targetXField.mouseClicked(mouseX, mouseY, mouseButton);
//        this.targetZField.mouseClicked(mouseX, mouseY, mouseButton);
//    }
//
//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        this.codeField.updateCursorCounter();
//        this.targetXField.updateCursorCounter();
//        this.targetZField.updateCursorCounter();
//
//        // LOGICA DI ABILITAZIONE BOTTONE (8 cifre + Coordinate Tra -2000 e 2000)
//        boolean isCodeValid = this.codeField.getText().length() == 8;
//        boolean isXValid = checkCoord(this.targetXField.getText());
//        boolean isZValid = checkCoord(this.targetZField.getText());
//
//        this.confirmButton.enabled = isCodeValid && isXValid && isZValid;
//    }
//
//    // Metodo helper per controllare se la coordinata è valida
//    private boolean checkCoord(String text) {
//        if (text.isEmpty() || text.equals("-")) return false;
//        try {
//            int val = Integer.parseInt(text);
//            return val >= -2000 && val <= 2000;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//
//        // Titolo Principale
//        this.drawCenteredString(this.fontRendererObj, "LAUNCH CODE:", this.width / 2, this.height / 2 - 60, 0xFF5555);
//        this.codeField.drawTextBox();
//
//        // Testo per le coordinate
//        this.drawString(this.fontRendererObj, "Target X:", this.width / 2 - 80, this.height / 2 - 17, 0xAAAAAA);
//        this.targetXField.drawTextBox();
//
//        this.drawString(this.fontRendererObj, "Target Z:", this.width / 2 + 5, this.height / 2 - 17, 0xAAAAAA);
//        this.targetZField.drawTextBox();
//
//        super.drawScreen(mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public void onGuiClosed() { Keyboard.enableRepeatEvents(false); }
//}