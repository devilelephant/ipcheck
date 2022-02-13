package com.gcoller.ipcheck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Loads an {@link IpTree} instance with the latest ipsets from the FIREHOL repo.
 */
@SuppressWarnings("SpellCheckingInspection")
@Slf4j
public class IpTreeLoader {

  public static final String FIREHOL_GITHUB_REPO = "https://github.com/firehol/blocklist-ipsets";

  public static void main(String[] args) {
    IpTree ipTree = new IpTree();
    new IpTreeLoader().load(ipTree);
  }

  public void load(IpTree tree) {
    var dir = new File("./build/firehol");
    try {
      log.info("updating local: repo={}", dir.getAbsoluteFile());
      updateLocalRepo(dir);

      log.info("walk repo file tree");
      var files = fetchFiles(dir.getAbsolutePath(), 10);
      log.info("parsing files: files={}", files.size());
      files.forEach(f -> addFile(tree, f));
      log.info("finshed");
    } catch (Exception e) {
      throw new IllegalStateException("WTF", e);
    }
  }

  private void updateLocalRepo(File dir) throws GitAPIException, IOException {
    if (dir.exists()) {
      log.info("git pull existing repo");
      Git.open(dir.getAbsoluteFile()).pull().call();
    } else {
      log.info("git clone new repo");
      if (!dir.mkdir()) {
        throw new IllegalStateException("Failed to make {}" + dir.getAbsolutePath());
      }
      Git.cloneRepository()
          .setURI(FIREHOL_GITHUB_REPO)
          .setDirectory(dir.getAbsoluteFile())
          .call();
    }
  }

  void addFile(IpTree tree, Path path) {
    try {
      var fileName = path.getFileName().toString();

      Files.lines(path)
          .filter(line -> !line.startsWith("#"))
          .forEach(line -> tree.add(fileName, line));
    } catch (IOException e) {
      log.error("error: path={}", path, e);
    }
  }

  Set<Path> fetchFiles(String dir, int depth) throws IOException {
    try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
      return stream
          .filter(file ->
              !Files.isDirectory(file)
                  && (file.toString().endsWith(".netset") || file.toString().endsWith(".ipset"))
          )
          .collect(Collectors.toSet());
    }
  }

}
