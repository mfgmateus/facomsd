package br.ufu.listener;

import br.ufu.exception.ListenerException;
import br.ufu.model.Command;
import br.ufu.service.QueueService;
import br.ufu.writer.LogWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigInteger;

public class F2Listener extends FxListener {

    private static final Logger log = LogManager.getLogger(F2Listener.class);
    private static final String FILE_SEPARATOR = "\n";
    private BigInteger logNumber = new BigInteger("0");
    private final BigInteger serverId;
    private final String logPath;

    private final QueueService queueService;

    private LogWriter logWriter;

    public F2Listener(QueueService queueService, String logPath, BigInteger serverId) {
        this.queueService = queueService;
        this.logPath = logPath;
        this.serverId = serverId;

        try {
            setNewLog();
        } catch (IOException e) {
            throw new ListenerException(e);
        }

    }

    private BigInteger getLogNumber() {
        logNumber = logNumber.add(new BigInteger("1"));
        return logNumber;
    }

    public void setNewLog() throws IOException {
        BigInteger logNumber = getLogNumber();
        LogWriter log = new LogWriter(logPath, logNumber, serverId);
        this.logWriter = log;
    }

    @Override
    protected void listen() {
        try {
            Command item = queueService.consumeF2();
            log.info("F2 Listener take command [{}]", item.getExecuteCommand());
            if (commadIsRead(item)) {
                return;
            }
            logWriter.write(item.getExecuteCommand() + FILE_SEPARATOR);
        } catch (InterruptedException | IOException e) {
            throw new ListenerException(e);
        }
    }

    private boolean commadIsRead(Command item) {
        return "READ".equals(item.getExecuteCommand().split(" ")[0]);
    }
}
