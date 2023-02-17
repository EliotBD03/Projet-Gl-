Vue.component('change-page', {
  props: ['go', 'name', 'css'],
  template: `
    <div>
        <a :href="go">
            <button :class="css">{{ name }}</button>
        </a>
    </div>
    `
});

new Vue({
  el: '#button_redirection_wallet'
});

new Vue({
  el: '#button_redirection_walletFull'
});

new Vue({
  el: '#button_redirection_addWallet'
});

new Vue({
  el: '#button_redirection_contracts'
});

new Vue({
  el: '#button_redirection_notifications'
});

new Vue({
  el: '#button_redirection_settings'
});

new Vue({
  el: '#button_redirection_newcontracts'
});

new Vue({
  el: '#button_redirection_disconnect'
});

new Vue({
  el: '#button_redirection_home'
});

