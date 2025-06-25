shijie@DT2-UBUNTU22LTS:~/keys$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/home/shijie/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/shijie/.ssh/id_rsa
Your public key has been saved in /home/shijie/.ssh/id_rsa.pub
The key fingerprint is:
SHA256:XG3vKHHsSQ0iujvTgHMN2JIH0qqiQbj0OyXyhEO928k shijie@DT2-UBUNTU22LTS
The key's randomart image is:
+---[RSA 3072]----+
|   .             |
|  . o      .     |
|. .o =  . o +    |
|.+..+ +o o + +   |
|+oo .+.oS . + o  |
|=+ =o.o..  = +   |
|o.= Booo  . + .  |
|.  = Eo..  .     |
|    . .o         |
+----[SHA256]-----+
shijie@DT2-UBUNTU22LTS:~/keys$



shijie@DT2-UBUNTU22LTS:~/.ssh$ ssh-copy-id -i id_rsa.pub shijie@DT2-UBUNTU22LTS
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "id_rsa.pub"
The authenticity of host 'dt2-ubuntu22lts (127.0.1.1)' can't be established.
ED25519 key fingerprint is SHA256:GsQrRSxHV+n746PVCkuVxNRB8VErQLFN7bu6f0adTMY.
This host key is known by the following other names/addresses:
    ~/.ssh/known_hosts:4: [hashed name]
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
shijie@dt2-ubuntu22lts's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'shijie@DT2-UBUNTU22LTS'"
and check to make sure that only the key(s) you wanted were added.



shijie@DT2-UBUNTU22LTS:~/.ssh$ ls
authorized_keys  id_rsa  id_rsa.pub  known_hosts  known_hosts.old

shijie@DT2-UBUNTU22LTS:~/.ssh$ cp id_rsa id_rsa.backup
shijie@DT2-UBUNTU22LTS:~/.ssh$ ls
authorized_keys  id_rsa  id_rsa.backup  id_rsa.pub  known_hosts  known_hosts.old
shijie@DT2-UBUNTU22LTS:~/.ssh$ ssh-keygen -p -m PEM -f id_rsa
Enter old passphrase:
Key has comment 'shijie@DT2-UBUNTU22LTS'
Enter new passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved with the new passphrase.
shijie@DT2-UBUNTU22LTS:~/.ssh$
