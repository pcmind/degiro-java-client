package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.model.DPortfolioProducts;
import com.google.gson.GsonBuilder;
import java.io.File;

/**
 *
 * @author indiketa
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DEngine e = new DEngine(new Credentials(new File("/home/casa/dg.properties")));


        e.start();
    }
}

//        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
//        Screen screen = null;
//
//        try {
//
//            terminalFactory.setTerminalEmulatorTitle("DeGiro - Beta");
//            terminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(new Font("DejaVu Sans Mono", Font.PLAIN, 12)));
//            terminalFactory.setForceAWTOverSwing(false);
//            screen = terminalFactory.createScreen();
//            screen.startScreen();
//
//    
//            DefaultWindowManager fwm = new DefaultWindowManager();
//            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.CYAN));
//
//
//            final Window window = new BasicWindow("My Root Window");
//
//            window.setSize(new TerminalSize(800, 24));
//
//            Table t = new Table("A");
//            TableModel tm = new TableModel("A", "B", "C");
//            for (int i = 0; i < 1000; i++) {
//                tm.addRow(i + "", i + "fgfdgdfg\ndgdfgdfg", i + "");
//            }
//            t.setCellSelection(true);
//            t.setVisibleRows(10);
//            t.setTableModel(tm);
//            final DefaultTableCellRenderer original = (DefaultTableCellRenderer) t.getTableCellRenderer();
//            t.setTableCellRenderer(new TableCellRenderer() {
//                @Override
//                public TerminalSize getPreferredSize(Table table, Object v, int i, int i1) {
//                    return original.getPreferredSize(table, v, i, i1);
//                }
//
//                @Override
//                public void drawCell(Table table, Object cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics) {
//                    ThemeDefinition themeDefinition = table.getThemeDefinition();
//                    if ((table.getSelectedColumn() == columnIndex && table.getSelectedRow() == rowIndex)
//                            || (table.getSelectedRow() == rowIndex && !table.isCellSelection())) {
//                        if (table.isFocused()) {
//                            textGUIGraphics.applyThemeStyle(themeDefinition.getActive());
//                        } else {
//                            textGUIGraphics.applyThemeStyle(themeDefinition.getSelected());
//                        }
//                        textGUIGraphics.fill(' ');  //Make sure to fill the whole cell first
//                    } else {
//                        textGUIGraphics.applyThemeStyle(themeDefinition.getNormal());
//                    }
//                    String[] lines = getContent(cell);
//                    int rowCount = 0;
//                    textGUIGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
//                    for (String line : lines) {
//                        textGUIGraphics.putString(0, rowCount++, line);
//                    }
//                }
//
//                private String[] getContent(Object cell) {
//                    String[] lines;
//                    if (cell == null) {
//                        lines = new String[]{""};
//                    } else {
//                        lines = cell.toString().split("\r?\n");
//                    }
//                    return lines;
//                }
//            });
//            t.setViewTopRow(2);
//            window.setComponent(t);
////            
////            /*
////            The window has no content initially, you need to call setComponent to populate it with something. In this
////            case, and quite often in fact, you'll want to use more than one component so we'll create a composite
////            'Panel' component that can hold multiple sub-components. This is where we decide what the layout manager
////            should be.
////             */
////            Panel contentPanel = new Panel(new GridLayout(2));
////
////            /**
////             * Lanterna contains a number of built-in layout managers, the
////             * simplest one being LinearLayout that simply arranges components
////             * in either a horizontal or a vertical line. In this tutorial,
////             * we'll use the GridLayout which is based on the layout manager
////             * with the same name in SWT. In the constructor above we have
////             * specified that we want to have a grid with two columns, below we
////             * customize the layout further by adding some spacing between the
////             * columns.
////             */
////            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
////            gridLayout.setHorizontalSpacing(3);
////
////            /*
////            One of the most basic components is the Label, which simply displays a static text. In the example below,
////            we use the layout data field attached to each component to give the layout manager extra hints about how it
////            should be placed. Obviously the layout data has to be created from the same layout manager as the container
////            is using, otherwise it will be ignored.
////             */
////            Label title = new Label("This is a label that spans two columns");
////            title.setLayoutData(GridLayout.createLayoutData(
////                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
////                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
////                    true, // Give the component extra horizontal space if available
////                    false, // Give the component extra vertical space if available
////                    2, // Horizontal span
////                    1));                  // Vertical span
////            contentPanel.addComponent(title);
////
////            /*
////            Since the grid has two columns, we can do something like this to add components when we don't need to
////            customize them any further.
////             */
////            contentPanel.addComponent(new Label("Text Box (aligned)"));
////            contentPanel.addComponent(
////                    new TextBox()
////                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));
////
////            /*
////            Here is an example of customizing the regular text box component so it masks the content and can work for
////            password input.
////             */
////            contentPanel.addComponent(new Label("Password Box (right aligned)"));
////            contentPanel.addComponent(
////                    new TextBox()
////                    .setMask('*')
////                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));
////
////            /*
////            While we are not going to demonstrate all components here, here is an example of combo-boxes, one that is
////            read-only and one that is editable.
////             */
////            contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
////
////            List<String> timezonesAsStrings = new ArrayList<String>();
////            for (String id : TimeZone.getAvailableIDs()) {
////                timezonesAsStrings.add(id);
////            }
////            ComboBox<String> readOnlyComboBox = new ComboBox<String>(timezonesAsStrings);
////            readOnlyComboBox.setReadOnly(true);
////            readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
////            contentPanel.addComponent(readOnlyComboBox);
////
////            contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
////            contentPanel.addComponent(
////                    new ComboBox<String>("Item #1", "Item #2", "Item #3", "Item #4")
////                    .setReadOnly(false)
////                    .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));
////
////            /*
////            Some user interactions, like buttons, work by registering callback methods. In this example here, we're
////            using one of the pre-defined dialogs when the button is triggered.
////             */
////            contentPanel.addComponent(new Label("Button (centered)"));
////            contentPanel.addComponent(new Button("Button", new Runnable() {
////                @Override
////                public void run() {
////                    MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK);
////                }
////            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));
////
////            /*
////            Close off with an empty row and a separator, then a button to close the window
////             */
////            contentPanel.addComponent(
////                    new EmptySpace()
////                    .setLayoutData(
////                            GridLayout.createHorizontallyFilledLayoutData(2)));
////            contentPanel.addComponent(
////                    new Separator(Direction.HORIZONTAL)
////                    .setLayoutData(
////                            GridLayout.createHorizontallyFilledLayoutData(2)));
////            contentPanel.addComponent(
////                    new Button("Close", new Runnable() {
////                        @Override
////                        public void run() {
////                            window.close();
////                        }
////                    }).setLayoutData(
////                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));
////
////            /*
////            We now have the content panel fully populated with components. A common mistake is to forget to attach it to
////            the window, so let's make sure to do that.
////             */
////            window.setComponent(contentPanel);
//
//            /*
//            Now the window is created and fully populated. As discussed above regarding the threading model, we have the
//            option to fire off the GUI here and then later on decide when we want to stop it. In order for this to work,
//            you need a dedicated UI thread to run all the GUI operations, usually done by passing in a
//            SeparateTextGUIThread object when you create the TextGUI. In this tutorial, we are using the conceptually
//            simpler SameTextGUIThread, which essentially hijacks the caller thread and uses it as the GUI thread until
//            some stop condition is met. The absolutely simplest way to do this is to simply ask lanterna to display the
//            window and wait for it to be closed. This will initiate the event loop and make the GUI functional. In the
//            "Close" button above, we tied a call to the close() method on the Window object when the button is
//            triggered, this will then break the even loop and our call finally returns.
//             */
//            textGUI.addWindow(window);
////            textGUI.addWindowAndWait(window.);
//
//            /*
//            When our call has returned, the window is closed and no longer visible. The screen still contains the last
//            state the TextGUI left it in, so we can easily add and display another window without any flickering. In
//            this case, we want to shut down the whole thing and return to the ordinary prompt. We just need to stop the
//            underlying Screen for this, the TextGUI system does not require any additional disassembly.
//             */
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (screen != null) {
//                try {
//                    screen.stopScreen();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//}
