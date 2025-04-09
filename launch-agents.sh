#!/bin/bash

for i in {1..3}; do
  open -na "Chromium" --args --user-data-dir="/tmp/chrome-profile-player-$i" --no-first-run --new-window "https://false-idols.cooperhanessian.com/ok"
done

# open -na "Chromium" --args --user-data-dir="/tmp/chrome-profile-host" --no-first-run --new-window "http://localhost:3001"