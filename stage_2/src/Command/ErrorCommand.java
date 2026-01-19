package Command;

public class ErrorCommand extends Command {
    public ErrorCommand(int line, String rawCommand) {
        super("Error", line, rawCommand);
    }
}
