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
  private final File baseDir;
  private final File repoDir;

  public IpTreeLoader(String dirName) {
    this.baseDir = new File(dirName);
    this.repoDir = new File(dirName, "firehof");
  }

  public static void main(String[] args) {
    IpTree ipTree = new IpTree();
    new IpTreeLoader("./build/repo").load(ipTree);
  }

  public void load(IpTree tree) {
    try {
      log.info("updating local: repo={}", repoDir.getAbsoluteFile());
      updateLocalRepo();

      log.info("walk repo file tree");
      var files = fetchFiles(repoDir.getAbsolutePath(), 10);
      log.info("parsing {} files", files.size());
      files.forEach(f -> addFile(tree, f));
      log.info("finshed");
    } catch (Exception e) {
      throw new IllegalStateException("WTF", e);
    }
  }

  private void updateLocalRepo() throws GitAPIException, IOException {
    if (!baseDir.exists()) {
      throw new IllegalStateException("Missing base dir " + baseDir);
    }

    if (!Files.isWritable(baseDir.toPath())) {
      throw new IllegalStateException("Unwritable path " + baseDir);
    }

    if (repoDir.exists()) {
      log.info("git pull existing repo");
      Git.open(repoDir.getAbsoluteFile()).pull().call();
    } else {
      log.info("git clone new repo");
      Git.cloneRepository()
          .setURI(FIREHOL_GITHUB_REPO)
          .setDirectory(repoDir.getAbsoluteFile())
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
