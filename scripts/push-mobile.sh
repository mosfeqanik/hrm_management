#!/usr/bin/env bash
# Commit and push only the mobile_app folder.
# Usage: bash scripts/push-mobile.sh ["commit message"]
set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

MSG="${1:-Update mobile_app}"
BRANCH="$(git branch --show-current)"

if [ -z "$(find mobile_app -type f -not -path '*/.git/*' 2>/dev/null | head -1)" ]; then
  echo "mobile_app has no files; git does not track empty folders." >&2
  exit 1
fi

git add -- mobile_app

if git diff --cached --quiet -- mobile_app; then
  echo "No changes in mobile_app to commit."
else
  # Commit only mobile_app, leaving anything else already staged untouched.
  git commit -m "$MSG" -- mobile_app
fi

git push -u origin "$BRANCH"
