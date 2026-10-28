---
name: pull
description: Pull the latest commits from the remote (origin) into the current branch, fast-forwarding when possible. Use when the user says "pull", "/pull", or asks to update their branch from the remote.
allowed-tools: Bash(git status:*), Bash(git branch:*), Bash(git log:*), Bash(git pull:*), Bash(git remote:*), Bash(git fetch:*), Bash(git merge-base:*), Bash(git rev-list:*), Bash(git stash:*)
---

# Pull

Bring the current branch up to date with its remote counterpart.

## Steps

1. Run `git status -sb` to get the current branch, its upstream, and whether there are uncommitted changes.
2. Run `git remote -v` to confirm a remote named `origin` exists. If none exists, tell the user and stop.
3. Run `git fetch origin`. If the branch has no upstream, use `origin/<branch>` as the target; if that doesn't exist either, tell the user and stop.
4. If there are uncommitted changes, ask whether to `git stash` them first. Do not discard them.
5. Compare with `git rev-list --left-right --count HEAD...<target>`:
   - **Behind only** → `git pull --ff-only`.
   - **Already up to date** → say so and stop.
   - **Ahead only** → nothing to pull; suggest `/push`.
   - **Diverged** → show `git log --oneline HEAD..<target>` and `git log --oneline <target>..HEAD`, then ask the user whether to rebase (`git pull --rebase`) or merge (`git pull --no-rebase`).
6. If `git merge-base HEAD <target>` finds no common ancestor, the histories are unrelated. Explain this and offer:
   - Merge both with `git pull --no-rebase --allow-unrelated-histories`, or
   - Have the user run `git checkout -B <branch> <target>` themselves to adopt the remote history.
7. If a merge or rebase hits conflicts, list the conflicted files and help resolve them. Do not abort without asking.
8. If changes were stashed, run `git stash pop` and report any conflicts.
9. Show the final `git log --oneline -5`.

## Rules

- Never run `git reset --hard`, `git checkout -B`, or anything else that drops local commits.
- Do not commit or push anything beyond what the merge/rebase itself creates.
- If `$ARGUMENTS` names a remote or branch (e.g. `upstream main`), use that instead of the defaults.
