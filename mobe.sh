#!/usr/bin/env bash

set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$ROOT_DIR/mobe-life-frontend"
BACKEND_DIR="$ROOT_DIR/mobe-life-backend"
MINIPROGRAM_DIR="$ROOT_DIR/mobe-life-miniprogram"
RUNTIME_DIR="$ROOT_DIR/.mobe_runtime"

FRONTEND_PORT="${FRONTEND_PORT:-3000}"
BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PID="$RUNTIME_DIR/frontend.pid"
BACKEND_PID="$BACKEND_DIR/app.pid"
FRONTEND_LOG="$RUNTIME_DIR/frontend.log"
BACKEND_LOG="$BACKEND_DIR/logs/start.log"
BACKEND_JAR="$BACKEND_DIR/target/mobe-life-backend-0.0.1-SNAPSHOT.jar"

LOCAL_API_ROOT="http://127.0.0.1:8080"
REMOTE_API_ROOT="http://39.106.162.13"

mkdir -p "$RUNTIME_DIR"

print_line() {
  printf '%s\n' '----------------------------------------'
}

pause() {
  printf '\n按回车返回菜单...'
  read -r _
}

ask_yes_no() {
  local message="$1"
  local answer
  printf '%s [y/N] ' "$message"
  read -r answer
  case "$answer" in
    y|Y|yes|YES) return 0 ;;
    *) return 1 ;;
  esac
}

pid_is_running() {
  local pid_file="$1"

  [ -f "$pid_file" ] || return 1

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"
  [ -n "$pid" ] || return 1
  ps -p "$pid" >/dev/null 2>&1
}

read_pid() {
  local pid_file="$1"
  cat "$pid_file" 2>/dev/null || true
}

kill_by_pid_file() {
  local name="$1"
  local pid_file="$2"

  if ! pid_is_running "$pid_file"; then
    rm -f "$pid_file"
    printf '%s 未在运行。\n' "$name"
    return 1
  fi

  local pid
  pid="$(read_pid "$pid_file")"
  printf '正在关闭 %s，PID: %s\n' "$name" "$pid"
  kill "$pid" >/dev/null 2>&1 || true

  for _ in 1 2 3 4 5 6 7 8 9 10; do
    if ! ps -p "$pid" >/dev/null 2>&1; then
      rm -f "$pid_file"
      printf '%s 已关闭。\n' "$name"
      return 0
    fi
    sleep 1
  done

  if ask_yes_no "$name 没有正常退出，是否强制关闭 PID $pid？"; then
    kill -9 "$pid" >/dev/null 2>&1 || true
    rm -f "$pid_file"
    printf '%s 已强制关闭。\n' "$name"
    return 0
  fi

  printf '%s 仍在运行。\n' "$name"
  return 1
}

kill_by_port_with_confirm() {
  local name="$1"
  local port="$2"
  local pids

  pids="$(lsof -ti tcp:"$port" 2>/dev/null || true)"
  if [ -z "$pids" ]; then
    printf '端口 %s 没有发现运行中的进程。\n' "$port"
    return 1
  fi

  printf '端口 %s 上的进程：\n%s\n' "$port" "$pids"
  if ask_yes_no "是否按端口关闭 ${name}？"; then
    printf '%s\n' "$pids" | xargs kill >/dev/null 2>&1 || true
    printf '已发送关闭信号。\n'
    return 0
  fi

  return 1
}

port_is_used() {
  local port="$1"
  [ -n "$(lsof -ti tcp:"$port" 2>/dev/null || true)" ]
}

start_frontend() {
  if pid_is_running "$FRONTEND_PID"; then
    printf '前端已在运行，PID: %s\n' "$(read_pid "$FRONTEND_PID")"
    return 0
  fi

  if port_is_used "$FRONTEND_PORT"; then
    printf '端口 %s 已被占用，前端可能已经在运行。可用“查看状态”或“关闭前端”处理。\n' "$FRONTEND_PORT"
    return 1
  fi

  if [ ! -d "$FRONTEND_DIR" ]; then
    printf '找不到前端目录：%s\n' "$FRONTEND_DIR"
    return 1
  fi

  printf '正在启动前端：http://127.0.0.1:%s\n' "$FRONTEND_PORT"
  (
    cd "$FRONTEND_DIR" || exit 1
    nohup pnpm dev --host 127.0.0.1 --port "$FRONTEND_PORT" > "$FRONTEND_LOG" 2>&1 &
    echo $! > "$FRONTEND_PID"
  )

  sleep 2

  if pid_is_running "$FRONTEND_PID"; then
    printf '前端启动成功，PID: %s\n' "$(read_pid "$FRONTEND_PID")"
    printf '日志：%s\n' "$FRONTEND_LOG"
  else
    printf '前端启动失败，请查看日志：%s\n' "$FRONTEND_LOG"
    return 1
  fi
}

stop_frontend() {
  if ! kill_by_pid_file "前端" "$FRONTEND_PID"; then
    kill_by_port_with_confirm "前端" "$FRONTEND_PORT" || true
  fi
}

restart_frontend() {
  stop_frontend
  sleep 1
  start_frontend
}

build_backend_if_needed() {
  if [ -f "$BACKEND_JAR" ]; then
    return 0
  fi

  printf '未找到后端 Jar，开始打包：%s\n' "$BACKEND_JAR"
  (
    cd "$BACKEND_DIR" || exit 1
    ./mvnw -DskipTests package
  )
}

start_backend() {
  if pid_is_running "$BACKEND_PID"; then
    printf '后端已在运行，PID: %s\n' "$(read_pid "$BACKEND_PID")"
    return 0
  fi

  if port_is_used "$BACKEND_PORT"; then
    printf '端口 %s 已被占用，后端可能已经在运行。可用“查看状态”或“关闭后端”处理。\n' "$BACKEND_PORT"
    return 1
  fi

  if [ ! -d "$BACKEND_DIR" ]; then
    printf '找不到后端目录：%s\n' "$BACKEND_DIR"
    return 1
  fi

  build_backend_if_needed || return 1

  mkdir -p "$BACKEND_DIR/logs"

  printf '正在启动后端：http://127.0.0.1:%s\n' "$BACKEND_PORT"
  (
    cd "$BACKEND_DIR" || exit 1
    nohup java -jar "$BACKEND_JAR" > "$BACKEND_LOG" 2>&1 &
    echo $! > "$BACKEND_PID"
  )

  sleep 2

  if pid_is_running "$BACKEND_PID"; then
    printf '后端启动成功，PID: %s\n' "$(read_pid "$BACKEND_PID")"
    printf '日志：%s\n' "$BACKEND_LOG"
  else
    printf '后端启动失败，请查看日志：%s\n' "$BACKEND_LOG"
    return 1
  fi
}

stop_backend() {
  if ! kill_by_pid_file "后端" "$BACKEND_PID"; then
    kill_by_port_with_confirm "后端" "$BACKEND_PORT" || true
  fi
}

restart_backend() {
  stop_backend
  sleep 1
  start_backend
}

normalize_api_root() {
  local api_root="$1"
  api_root="${api_root%/}"
  api_root="${api_root%/api}"
  printf '%s' "$api_root"
}

replace_or_append_env() {
  local file="$1"
  local key="$2"
  local value="$3"

  touch "$file"

  if grep -q "^${key}=" "$file"; then
    KEY="$key" VALUE="$value" perl -0pi -e 'BEGIN { $key = $ENV{"KEY"}; $value = $ENV{"VALUE"} } s/^\Q$key\E=.*/$key . "=" . $value/gme' "$file"
  else
    printf '\n%s=%s\n' "$key" "$value" >> "$file"
  fi
}

update_frontend_api() {
  local api_root="$1"
  local env_file="$FRONTEND_DIR/.env"
  local nuxt_file="$FRONTEND_DIR/nuxt.config.ts"

  replace_or_append_env "$env_file" "NUXT_PUBLIC_API_BASE" "/api"

  if [ -f "$nuxt_file" ]; then
    API_PROXY_TARGET="${api_root}/api" perl -0pi -e 'BEGIN { $target = $ENV{"API_PROXY_TARGET"} } s#^\s*(?:target:\s*)?["\x27][^"\x27]+/api["\x27],#        target: \x27$target\x27,#gm' "$nuxt_file"
  fi

  printf '前端已切换为：/api -> %s/api\n' "$api_root"
}

update_miniprogram_api() {
  local api_root="$1"
  local config_file="$MINIPROGRAM_DIR/config/index.js"
  local index_file="$MINIPROGRAM_DIR/pages/index/index.js"

  if [ -f "$config_file" ]; then
    API_ROOT="$api_root" perl -0pi -e 'BEGIN { $root = $ENV{"API_ROOT"} } s#(^\s*baseUrl:\s*)["'\''][^"'\'']+["'\'']#$1 . "'\''" . $root . "'\''"#gme' "$config_file"
    printf '小程序 config baseUrl 已同步为：%s\n' "$api_root"
  fi

  if [ -f "$index_file" ]; then
    API_ROOT="$api_root" perl -0pi -e 'BEGIN { $root = $ENV{"API_ROOT"} } s#url:\s*["'\'']https?://[^"'\'']+/api/tool/daily-quote["'\'']#"url: '\''" . $root . "/api/tool/daily-quote'\''"#ge; s#url:\s*["'\'']https?://[^"'\'']+/api/tool/weather["'\'']#"url: '\''" . $root . "/api/tool/weather'\''"#ge' "$index_file"
    printf '小程序首页直写接口已同步。\n'
  fi
}

update_backend_upload_url() {
  local api_root="$1"
  local upload_url="${api_root}/uploads"
  local file

  for file in "$BACKEND_DIR/src/main/resources/application-dev.yml" "$BACKEND_DIR/src/main/resources/application-prod.yml"; do
    [ -f "$file" ] || continue
    UPLOAD_URL="$upload_url" perl -0pi -e 'BEGIN { $url = $ENV{"UPLOAD_URL"} } s#(access-url-prefix:\s*)[^\n]+#$1 . $url#ge' "$file"
    printf '上传文件访问前缀已同步：%s\n' "$file"
  done
}

switch_api_menu() {
  local choice
  local api_root

  print_line
  printf '选择前端连接的后端地址\n'
  print_line
  printf '1. 本地后端 %s\n' "$LOCAL_API_ROOT"
  printf '2. 服务器后端 %s\n' "$REMOTE_API_ROOT"
  printf '3. 自定义地址\n'
  printf '0. 返回\n'
  printf '请输入选项：'
  read -r choice

  case "$choice" in
    1) api_root="$LOCAL_API_ROOT" ;;
    2) api_root="$REMOTE_API_ROOT" ;;
    3)
      printf '请输入后端根地址，例如 http://127.0.0.1:8080 或 http://example.com：'
      read -r api_root
      if [ -z "$api_root" ]; then
        printf '地址不能为空。\n'
        return 1
      fi
      ;;
    0) return 0 ;;
    *)
      printf '无效选项。\n'
      return 1
      ;;
  esac

  api_root="$(normalize_api_root "$api_root")"

  update_frontend_api "$api_root"

  if ask_yes_no "是否同步小程序接口地址？"; then
    update_miniprogram_api "$api_root"
  fi

  if ask_yes_no "是否同步后端上传文件访问前缀？"; then
    update_backend_upload_url "$api_root"
  fi

  printf '地址切换完成。若前端正在运行，请重启前端让配置生效。\n'
}

show_status() {
  local frontend_status="未运行"
  local backend_status="未运行"
  local api_base="-"
  local proxy_target="-"
  local miniprogram_base="-"

  if pid_is_running "$FRONTEND_PID"; then
    frontend_status="运行中，PID: $(read_pid "$FRONTEND_PID")"
  elif port_is_used "$FRONTEND_PORT"; then
    frontend_status="端口 ${FRONTEND_PORT} 被占用，可能是手动启动"
  fi

  if pid_is_running "$BACKEND_PID"; then
    backend_status="运行中，PID: $(read_pid "$BACKEND_PID")"
  elif port_is_used "$BACKEND_PORT"; then
    backend_status="端口 ${BACKEND_PORT} 被占用，可能是手动启动"
  fi

  if [ -f "$FRONTEND_DIR/.env" ]; then
    api_base="$(grep '^NUXT_PUBLIC_API_BASE=' "$FRONTEND_DIR/.env" | tail -n 1 | cut -d= -f2- || true)"
  fi

  if [ -f "$FRONTEND_DIR/nuxt.config.ts" ]; then
    proxy_target="$(grep -E "target: ['\"]" "$FRONTEND_DIR/nuxt.config.ts" | head -n 1 | sed -E "s/.*target: ['\"]([^'\"]+)['\"].*/\1/" || true)"
  fi

  if [ -f "$MINIPROGRAM_DIR/config/index.js" ]; then
    miniprogram_base="$(grep -E '^\s*baseUrl:' "$MINIPROGRAM_DIR/config/index.js" | head -n 1 | sed -E "s/.*baseUrl: ['\"]([^'\"]+)['\"].*/\1/" || true)"
  fi

  print_line
  printf '运行状态\n'
  print_line
  printf '前端：%s\n' "$frontend_status"
  printf '后端：%s\n' "$backend_status"
  printf '前端 API_BASE：%s\n' "$api_base"
  printf 'Nuxt /api 代理：%s\n' "$proxy_target"
  printf '小程序 baseUrl：%s\n' "$miniprogram_base"
}

main_menu() {
  while true; do
    clear
    printf 'MoBe Life 开发助手\n'
    print_line
    printf '1. 启动前端\n'
    printf '2. 启动后端\n'
    printf '3. 关闭前端\n'
    printf '4. 关闭后端\n'
    printf '5. 重启前端\n'
    printf '6. 重启后端\n'
    printf '7. 切换接口地址\n'
    printf '8. 查看状态\n'
    printf '0. 退出\n'
    print_line
    printf '请输入选项：'

    local choice
    read -r choice

    case "$choice" in
      1) start_frontend; pause ;;
      2) start_backend; pause ;;
      3) stop_frontend; pause ;;
      4) stop_backend; pause ;;
      5) restart_frontend; pause ;;
      6) restart_backend; pause ;;
      7) switch_api_menu; pause ;;
      8) show_status; pause ;;
      0) exit 0 ;;
      *) printf '无效选项。\n'; pause ;;
    esac
  done
}

main_menu
