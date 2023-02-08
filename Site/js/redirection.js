Vue.component('change-page', {
  props: ['go'],
  template: `
    <div>
        <a :href="go">
            <button class="button">Go</button>
        </a>
    </div>
    `
});

new Vue({
  el: '#button_redirection_wallet'
});

new Vue({
  el: '#button_redirection_contracts'
});

new Vue({
  el: '#button_redirection_notifications'
});

