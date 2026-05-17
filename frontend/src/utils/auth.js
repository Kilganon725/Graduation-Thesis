import { ref } from 'vue'

const TOKEN_KEY = 'aifomo_token'
const USER_KEY = 'aifomo_user'

export const authState = ref({
  token: localStorage.getItem(TOKEN_KEY),
  user: (() => {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  })()
})

export function setAuth(token, user) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
  authState.value = { token, user }
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getUserInfo() {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? JSON.parse(raw) : null
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
  authState.value = { token: null, user: null }
}
