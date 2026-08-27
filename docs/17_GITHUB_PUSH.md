# 17 — GitHub Repository

Canonical remote repository:

`https://github.com/jafarovsamir89/EfficientMachine`

Default branch: `main`.

## Clone

```bash
git clone https://github.com/jafarovsamir89/EfficientMachine.git
cd EfficientMachine
```

## Push an existing local checkout

```bash
git remote remove origin 2>/dev/null || true
git remote add origin https://github.com/jafarovsamir89/EfficientMachine.git
git push -u origin main
```

## Working policy

- Keep `main` stable.
- Use short-lived feature branches for Android implementation work.
- Prefer small, reviewable commits grouped by feature.
- Do not force-push `main`.
- Update GDD/technical docs when gameplay rules or architecture materially change.
